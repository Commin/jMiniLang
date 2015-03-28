package priv.bajdcc.syntax.token;

/**
 * ��������
 * 
 * @author bajdcc
 */
public enum TokenType {
	TERMINAL("�ս��"), NONTERMINAL("���ս��"), EOF("ȫ��ĩβ"), COMMENT("ע��"), OPERATOR(
			"������"), WHITSPACE("�հ��ַ�"), HANDLER("��������"), ERROR("����");

	private String name;

	TokenType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
};
