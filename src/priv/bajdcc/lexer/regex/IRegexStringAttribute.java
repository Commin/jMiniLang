package priv.bajdcc.lexer.regex;

/**
 * ƥ����Ϣ
 * 
 * @author bajdcc
 *
 */
public interface IRegexStringAttribute {

	/**
	 * ����ƥ����
	 * 
	 * @param result
	 *            ƥ����
	 */
	public void setResult(String result);

	/**
	 * ����ƥ����
	 * 
	 * @return ƥ����
	 */
	public String getResult();

	/**
	 * ����̰��ģʽ
	 * 
	 * @return �Ƿ�Ϊ̰��ģʽ
	 */
	public boolean getGreedMode();
}
