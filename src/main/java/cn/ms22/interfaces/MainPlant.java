package cn.ms22.interfaces;

import cn.ms22.GuokeApplication;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 果壳界面
 */
public class MainPlant extends JFrame {
    private static final long serialVersionUID = 1L;

    Color mainPaneColor = new Color(230, 230, 250);
    //面板
    JLayeredPane layeredPane = new JLayeredPane();// 面板层
    JPanel mainPanel = new JPanel(); // 登陆面板
    //label
    final JLabel labelRunEnv = new JLabel("运行环境:");
    final JLabel labelGuoke = new JLabel("jar包:");
    final JLabel labelDriver = new JLabel("谷歌驱动:");
    final JLabel labelParallels = new JLabel("并行数量:");
    final JLabel labelPasswords = new JLabel("账号地址:");
    final JLabel labelOutput = new JLabel("文件输出地址:");
    final JLabel labelPlace = new JLabel("国内/国外:");
    final JLabel labelLog = new JLabel("日志:");

    //button
    JButton buttonOk = new JButton("开始"); // 确认按钮
    JButton buttonRepair = new JButton("重试");
    JButton buttonCancel = new JButton("关闭"); //取消按钮
    //资源管理器
    JButton buttonInitEnv = new JButton("初始化");
    JButton buttonGuokeJar = new JButton("选择jar");
    JButton buttonDriver = new JButton("选择驱动");
    JButton buttonPasswords = new JButton("选择passwords");
    JButton buttonOutput = new JButton("选择文档输出位置");
    JButton buttonOutputOpen = new JButton("打开文件位置");

    //value
    JComboBox comboBoxPlace = null;//国内外
    JTextField textRunEnv = new JTextField();
    JTextField textGuokeJar = new JTextField();
    JTextField textDriver = new JTextField();
    JTextField textParallels = new JTextField();
    JTextField textPasswords = new JTextField();
    JTextField textOutput = new JTextField();
    JTextArea textAreaLog = new JTextArea();

    // 用于处理拖动事件，表示鼠标按下时的坐标，相对于JFrame
    int xOld = 0;
    int yOld = 0;

    public MainPlant() {
        super();
        //初始化环境
        initialize();
        //自定义日志打印
        logThreadInit();
        //环境查找并自动配置
        envConfig();
    }

    private void envConfig() {
        Thread configThread = new Thread(() -> {
            //运行环境
            this.appendLogInfo("自动配置环境参数...");
            String runEnv = System.getProperties().getProperty("java.home");
            this.appendLogInfo(runEnv);
            textRunEnv.setText(runEnv);
            textRunEnv.setEditable(false);
            //
            textGuokeJar.setEnabled(false);
            textParallels.setText("5");
            textDriver.setText("C:\\Users\\Administrator\\Desktop\\guoke\\chromedriver.exe");
            textDriver.setEditable(false);
            textOutput.setText("C:\\Users\\Administrator\\Desktop\\guoke\\output");
            textOutput.setEditable(false);
            textPasswords.setText("C:\\Users\\Administrator\\Desktop\\guoke\\sonkwo.passwd");
            textPasswords.setEditable(false);
        });
        configThread.start();
    }

    /**
     * 打印日志
     */
    private void logThreadInit() {
        Thread bgLogThread = new Thread(() -> {
            this.appendLogInfo("启动打印线程...");
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    if (!LogBuffer.isEmp()) {
                        this.textAreaLog.append(LogBuffer.get());
                    }
                    if (this.textAreaLog.getLineCount() > 200) {
                        String showText = this.textAreaLog.getText();
                        showText = showText.substring(showText.indexOf("\n") + 1);
                        this.textAreaLog.setText(showText);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        bgLogThread.setDaemon(true);
        bgLogThread.setName("logThread");
        bgLogThread.start();
    }

    public void appendLogInfo(String logInfo) {
        LogBuffer.put(logInfo);
    }

    //初始化界面
    private void initialize() {
        //主界面
        this.setTitle("杉果数据获取器");
        // 时间绑定
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xOld = e.getX();
                yOld = e.getY();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int xOnScreen = e.getXOnScreen();
                int yOnScreen = e.getYOnScreen();

                int xx = xOnScreen - xOld;
                int yy = yOnScreen - yOld;
                MainPlant.this.setLocation(xx, yy);
            }
        });

        //底部布局面板
        layeredPane.setBounds(0, 0, 620, 650);
        this.add(layeredPane);

        //主界面
        mainPanel.setBounds(15, 10, 715, 650);
        mainPanel.setBackground(mainPaneColor);
        mainPanel.setLayout(null);
        layeredPane.add(mainPanel);

        //label
        mainPanel.add(labelRunEnv);
        labelRunEnv.setBounds(new Rectangle(25, 40, 100, 25));
        mainPanel.add(labelGuoke);
        labelGuoke.setBounds(new Rectangle(25, 70, 220, 25));
        mainPanel.add(labelDriver);
        labelDriver.setBounds(new Rectangle(25, 100, 220, 25));
        mainPanel.add(labelOutput);
        labelOutput.setBounds(new Rectangle(25, 130, 220, 25));
        mainPanel.add(labelParallels);
        labelParallels.setBounds(new Rectangle(25, 160, 220, 25));
        mainPanel.add(labelPasswords);
        labelPasswords.setBounds(new Rectangle(25, 190, 220, 25));
        mainPanel.add(labelPlace);
        labelPlace.setBounds(new Rectangle(25, 220, 220, 25));
        mainPanel.add(labelLog);
        labelLog.setBounds(new Rectangle(25, 250, 220, 25));

        //设置值
        mainPanel.add(textRunEnv);
        textRunEnv.setBounds(new Rectangle(120, 40, 240, 25));
        mainPanel.add(textGuokeJar);
        textGuokeJar.setBounds(new Rectangle(120, 70, 240, 25));
        mainPanel.add(textDriver);
        textDriver.setBounds(new Rectangle(120, 100, 240, 25));
        mainPanel.add(textOutput);
        textOutput.setBounds(new Rectangle(120, 130, 240, 25));
        mainPanel.add(textParallels);
        textParallels.setBounds(new Rectangle(120, 160, 240, 25));
        mainPanel.add(textPasswords);
        textPasswords.setBounds(new Rectangle(120, 190, 240, 25));
        //下拉
        String[] defaultPlace = {"INNER_QUEUE", "OUTER_QUEUE", "MIXTURE"};
        comboBoxPlace = new JComboBox(defaultPlace);
        mainPanel.add(comboBoxPlace);
        comboBoxPlace.setBounds(new Rectangle(120, 220, 240, 25));
        comboBoxPlace.setEditable(true);
        //日志
        JPanel scrollLogPanel = new JPanel();
        scrollLogPanel.setBounds(new Rectangle(25, 280, 662, 300));
        scrollLogPanel.add(new JScrollPane(textAreaLog));
        mainPanel.add(scrollLogPanel);

        textAreaLog.setLineWrap(true);
        textAreaLog.setColumns(59);
        textAreaLog.setRows(18);
        textAreaLog.setEditable(false);

        //资源管理器选择
        mainPanel.add(buttonInitEnv);
        buttonInitEnv.setBounds(new Rectangle(380, 40, 150, 25));
        mainPanel.add(buttonGuokeJar);
        buttonGuokeJar.setEnabled(false);
        buttonGuokeJar.setBounds(new Rectangle(380, 70, 150, 25));
        mainPanel.add(buttonDriver);
        buttonDriver.setBounds(new Rectangle(380, 100, 150, 25));
        mainPanel.add(buttonOutput);
        buttonOutput.setBounds(new Rectangle(380, 130, 150, 25));
        mainPanel.add(buttonOutputOpen);
        buttonOutputOpen.setBounds(new Rectangle(550, 130, 150, 25));
        mainPanel.add(buttonPasswords);
        buttonPasswords.setBounds(new Rectangle(380, 190, 150, 25));

        //启动关闭按钮位置
        mainPanel.add(buttonOk);
        buttonOk.setBounds(new Rectangle(120, 600, 80, 25));
        mainPanel.add(buttonRepair);
        buttonRepair.setBounds(new Rectangle(320, 600, 80, 25));
        mainPanel.add(buttonCancel);
        buttonCancel.setBounds(new Rectangle(520, 600, 80, 25));

        //事件绑定
        //驱动选择
        buttonDriver.addActionListener(e -> {
            JFileChooser fd = new JFileChooser();
            fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fd.showOpenDialog(null);
            fd.setFileFilter(new FileNameExtensionFilter("必须是可执行文件", "exe"));
            File f = fd.getSelectedFile();
            if (f == null) {
                LogBuffer.put("需要选择exe可执行文件。");
            }
            if (f != null) {
                LogBuffer.put("选择文件路径：" + f.getPath());
                textDriver.setText(f.getPath());
            }
        });
        //initEnv
        buttonInitEnv.addActionListener(e -> {
            String initArgs = JOptionPane.showInputDialog(this, "输入初始化信息",
                    "初始化", JOptionPane.INFORMATION_MESSAGE);
            if (!("".equals(initArgs) || initArgs == null)) {

                LogBuffer.put(initArgs);
                for (String arg : initArgs.split(" ")) {
                    String[] arTmp = arg.split("=");
                    String argName = arTmp[0];
                    String value;
                    try {
                        value = arTmp[1].replace("\\\\", "\\").replace("\"", "");
                    } catch (ArrayIndexOutOfBoundsException e1) {
                        continue;
                    }
                    switch (argName.trim().isEmpty() ? "error" : argName.trim().toLowerCase()) {
                        case "driver":
                            textDriver.setText(value);
                            LogBuffer.put("驱动位置:" + value);
                            break;
                        case "parallels":
                            textParallels.setText(value);
                            LogBuffer.put("并行线程数量:" + value);
                            break;
                        case "passwords":
                            textPasswords.setText(value);
                            LogBuffer.put("用户名路径:" + value);
                            break;
                        case "output":
                            textOutput.setText(value);
                            LogBuffer.put("输出路径:" + value);
                            break;
                        default:
                            LogBuffer.put("其他参数：" + argName);
                    }
                }
            }
        });
        //jar，不需要选择
        buttonGuokeJar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.showOpenDialog(this);
            fileChooser.setAcceptAllFileFilterUsed(false);
            ExtensionFileFilter filter = new ExtensionFileFilter();
            filter.addExtension("jar");
            filter.setDescription("必须是Jar文件");
            fileChooser.addChoosableFileFilter(filter);
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                LogBuffer.put("必须是Jar文件。");
            }
            if (selectedFile != null) {
                LogBuffer.put("选择文件路径：" + selectedFile.getPath());
                textGuokeJar.setText(selectedFile.getPath());
            }
        });

        //用户名选择
        buttonPasswords.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.showOpenDialog(this);
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                LogBuffer.put("需要选择用户名文件。");
            }
            if (selectedFile != null) {
                LogBuffer.put("选择文件路径：" + selectedFile.getPath());
                textPasswords.setText(selectedFile.getPath());
            }
        });
        //输出地址
        buttonOutput.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showOpenDialog(this);
            fileChooser.setFileFilter(new FileNameExtensionFilter("必须是文件路径", "."));
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                LogBuffer.put("需要选择保存文件路径。");
            }
            if (selectedFile != null) {
                LogBuffer.put("选择文件路径：" + selectedFile.getPath());
                textOutput.setText(selectedFile.getPath());
            }
        });

        //打开文件夹
        buttonOutputOpen.addActionListener(e -> {
            if ("".equals(textOutput.getText())) {
                LogBuffer.put("未找到需要打开的文件夹");
                return;
            }
            try {
                Desktop.getDesktop().open(new File(textOutput.getText()));
            } catch (Exception ex) {
                LogBuffer.put("未找到" + textOutput.getText() + "文件夹");
            }
        });

        //启动
        buttonOk.addActionListener(e -> {
            List<String> args = new ArrayList<>(1 << 3);
            String driver = textDriver.getText();
            String parallels = textParallels.getText();
            String passwords = textPasswords.getText();
            String output = textOutput.getText();
            String place = Objects.requireNonNull(comboBoxPlace.getSelectedItem()).toString();
            LogBuffer.put("driver=" + driver);
            LogBuffer.put("parallels=" + parallels);
            LogBuffer.put("passwords=" + passwords);
            LogBuffer.put("output=" + output);
            LogBuffer.put("place=" + place);

            args.add("driver=" + driver);
            args.add("parallels=" + parallels);
            args.add("passwords=" + passwords);
            args.add("output=" + output);
            args.add("place=" + place);

            new Thread(() -> {
                GuokeApplication.run0(args.toArray(new String[0]));
            }).start();
        });

        buttonRepair.addActionListener(e -> {
            //todo 失败重试
            LogBuffer.put("失败重试功能，正在调试。。");
        });

        //退出
        buttonCancel.addActionListener(e -> {
            System.exit(0);
        });

        //主界面设置
        this.setBounds(0, 0, 750, 740);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class ExtensionFileFilter extends FileFilter {
        private String description;
        private ArrayList<String> extensions = new ArrayList<>();

        //自定义方法，用于添加文件后缀名
        public void addExtension(String extension) {
            if (!extension.startsWith("."))
                extension = "." + extension;
            extensions.add(extension.toLowerCase());
        }

        //用于设置该文件过滤器的描述文本
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) return true;
            String name = file.getName().toLowerCase();
            for (String extension : extensions) {
                if (name.endsWith(extension)) return true;
            }
            return false;
        }
    }


    public static void main(String[] args) {
        MainPlant mainPlant = new MainPlant();
/*        Thread appendThread = new Thread(() -> {
            Random random = new Random();
            System.out.println("启动填充线程");
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainPlant.appendLogInfo("你是谁");
            }
        });
        appendThread.start();*/
    }
}
