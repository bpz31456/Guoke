package cn.ms22;

import cn.ms22.config.ApplicationConfig;
import cn.ms22.entity.CodeOrder;
import cn.ms22.entity.PassWord;
import cn.ms22.persistence.*;
import cn.ms22.pool.CatchThreadPoolFactory;
import cn.ms22.task.RegisterTask;
import cn.ms22.task.TaskFactory;
import cn.ms22.utils.DateTools;
import cn.ms22.utils.FileTools;
import cn.ms22.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author baopz
 */
public class GuokeApplication {
    private static Logger logger = LoggerFactory.getLogger(GuokeApplication.class);
    private final static String MAIN_APPLICATION_FETCH = "MAIN_APPLICATION_FETCH";
    private final static String MAIN_APPLICATION_REGISTER = "MAIN_APPLICATION_REGISTER";

    //默认初始化参数
    static {
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        applicationConfig.setDriver(PropertiesUtil.getProperty("guoke.properties", "driver"));
        applicationConfig.setOutput(PropertiesUtil.getProperty("guoke.properties", "output"));
        String parallels = PropertiesUtil.getProperty("guoke.properties", "parallels");
        applicationConfig.setParallels(Integer.valueOf(parallels == null ? "0" : parallels));
        applicationConfig.setPasswords(PropertiesUtil.getProperty("guoke.properties", "passwords"));
        applicationConfig.setRunApplication(PropertiesUtil.getProperty("guoke.properties", "runApplication"));
    }

    public static void main(String[] args) {
        String startBranch = null;
        if (args == null || args.length == 0) {
            startBranch = ApplicationConfig.getInstance().getRunApplication();
            if (startBranch == null || "".equals(startBranch)) {
                logger.warn("没有有效启动参数。");
                return;
            }
        } else {
            for (String arg : args) {
                if (arg.contains("runApplication=MAIN_APPLICATION_REGISTER")) {
                    startBranch = MAIN_APPLICATION_REGISTER;
                    break;
                }
                if (arg.contains("runApplication=MAIN_APPLICATION_FETCH")) {
                    startBranch = MAIN_APPLICATION_FETCH;
                    break;
                }
            }
        }
        logger.info("加载客户端接受的参数。");
        assert args != null;
        if (startBranch != null && startBranch.equals(MAIN_APPLICATION_REGISTER)) {
            run1(args);
        } else {
            run0(args);
        }
    }

    /**
     * register
     *
     * @param args
     */
    private static void run1(String[] args) {
        for (String arg : args) {
            String[] arTmp = arg.split("=");
            String argName = arTmp[0];
            String value = arTmp[1];
            switch (argName) {
                case "driver":
                    ApplicationConfig.getInstance().setDriver(value);
                    logger.info("驱动位置:{}", value);
                    break;
                case "output":
                    ApplicationConfig.getInstance().setOutput(value);
                    logger.info("输出路径:{}", value);
                    break;
                default:
                    logger.warn("参数{}无效", argName);
            }
        }
        ThreadPoolExecutor mainRegisterThread = CatchThreadPoolFactory.getSingleInstance("main_register", "main_register");
        mainRegisterThread.execute(new RegisterTask());
    }

    /**
     * fetch
     *
     * @param args
     */
    private static void run0(String[] args) {
        for (String arg : args) {
            String[] arTmp = arg.split("=");
            String argName = arTmp[0];
            String value = arTmp[1];
            switch (argName) {
                case "driver":
                    ApplicationConfig.getInstance().setDriver(value);
                    logger.info("驱动位置:{}", value);
                    break;
                case "parallels":
                    ApplicationConfig.getInstance().setParallels(Integer.valueOf(value));
                    logger.info("并行线程数量:{}", value);
                    break;
                case "passwords":
                    ApplicationConfig.getInstance().setPasswords(value);
                    logger.info("用户名路径:{}", value);
                    break;
                case "output":
                    ApplicationConfig.getInstance().setOutput(value);
                    logger.info("输出路径:{}", value);
                    break;
                default:
                    logger.warn("参数{}有误", argName);
            }
        }
        logger.info("应用启动，任务开始执行。。。");
        //加载配置参数

        //线程池
        ThreadPoolExecutor executor = CatchThreadPoolFactory.getInstance("默认采集线程", "defaultOrderFetch");

        //获取用户名，密码
        List<PassWord> passwds = resolvePassword();

        if (passwds == null) {
            logger.warn("未发现账户信息。");
            return;
        }

        //并行
        Semaphore semaphore = new Semaphore(ApplicationConfig.getInstance().getParallels());
        CountDownLatch countDownLatch = new CountDownLatch(passwds.size());

        for (PassWord passwd : passwds) {
            executor.execute(TaskFactory.getOrderTaskInstance(passwd, semaphore, countDownLatch));
        }

        //第一次处理
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("countDownLatch await 失败。");
        }
        //持久化
        try {
            txtInsert(QueueL.INNER_QUEUE);
            txtInsert(QueueL.OUTER_QUEUE);
            excelInsert(QueueL.MIXTURE);
        } catch (IOException e) {
            logger.error("数据持久化失败。");
        } finally {
            executor.shutdown();
        }

        //再次失败，数据保存到文件
        Path overflowPath = Paths.get(ApplicationConfig.getInstance().getOutput());
        for (PassWord passwd : QueueLR.getInstance()) {
            logger.info("错误任务保存");
            overflowPath = overflowPath.resolve(DateTools.currentDate() + "errorPasswd" + FileTools.FILE_TYPE_TXT);
            try {
                FileTools.appendInfo(overflowPath, passwd.toString());
            } catch (Exception e) {
                logger.error("保存未采集帐号数据失败，采用日志方式保存.");
                for (PassWord p1 : QueueLR.getInstance()) {
                    logger.warn("位正确采集的帐号信息:{}", p1.toString());
                }
            }
        }
    }

    private static void excelInsert(String key) throws IOException {
        logger.debug("执行Excel格式持久化。。。");
        String outputPath = ApplicationConfig.getInstance().getOutput();

        //分组保存，国际
        Stream<CodeOrder> stream = QueueL.getInstance(key).parallelStream();
        List<List<CodeOrder>> groupOrders = new ArrayList<>();
        stream.collect(Collectors.groupingBy(CodeOrder::getName, Collectors.toList()))
                .forEach((name, orderListByName) -> {
                    groupOrders.add(orderListByName);
                });
        for (List<CodeOrder> groupOrderSubList : groupOrders) {
            Path path = Paths.get(outputPath).resolve(key).resolve(DateTools.currentDate() + groupOrderSubList.get(0).getName() + FileTools.FILE_TYPE_XLS);
            DataPersistence dataPersistence = new ExcelPersistence(path);
            dataPersistence.save(groupOrderSubList);
        }
    }

    private static void txtInsert(String key) throws IOException {
        logger.debug("执行txt格式持久化。。。");
        String outputPath = ApplicationConfig.getInstance().getOutput();

        //分组保存，国际
        Stream<CodeOrder> stream = QueueL.getInstance(key).stream();
        List<List<CodeOrder>> groupOrders = new ArrayList<>();
        stream.collect(Collectors.groupingBy(CodeOrder::getName, Collectors.toList()))
                .forEach((name, orderListByName) -> {
                    groupOrders.add(orderListByName);
                });
        for (List<CodeOrder> groupOrderSubList : groupOrders) {
            Path path = Paths.get(outputPath).resolve(key).resolve(DateTools.currentDate() + groupOrderSubList.get(0).getName() + FileTools.FILE_TYPE_TXT);
            DataPersistence dataPersistence = new TxtPersistence(path);
            dataPersistence.save(groupOrderSubList);
        }
    }

    /**
     * 解析用户名，密码
     *
     * @return
     * @throws IOException
     */
    private static List<PassWord> resolvePassword() {
        String passwordPath = ApplicationConfig.getInstance().getPasswords();
        try {
            Path path = Paths.get(passwordPath);
            List<String> passwds = Files.readAllLines(path);
            return passwds.stream().map(s -> {
                String[] tmps = s.trim().split("\\s");
                PassWord passwd = null;
                if (tmps.length == 2) {
                    passwd = new PassWord();
                    String username = tmps[0];
                    String password = tmps[1];
                    passwd.setPassword(password);
                    passwd.setUsername(username);
                } else if (tmps.length == 1) {
                    passwd = new PassWord();
                    String username = tmps[0];
                    passwd.setPassword(username);
                    passwd.setUsername(username);
                } else {
                    logger.error("用户名配置文件格式[1.passwd]有误.");
                }
                return passwd;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置文件【{}】失败。", passwordPath);
        }
        return null;
    }
}
