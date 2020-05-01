package cn.ms22.entity;

/**
 * @author baopz
 */
public final class CodeOrderFactory {
    /**
     * 构造CodeOrder
     * @return
     */
    public static CodeOrder create(String username,String name,String code,String platform){
        CodeOrder codeOrder = new CodeOrder();
        codeOrder.setUsername(username);
        codeOrder.setName( name);
        codeOrder.setCode(code);
        codeOrder.setPlatform(platform);
        return codeOrder;
    }
}
