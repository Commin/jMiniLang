package priv.bajdcc.syntax;

/**
 * �Ǻſ�����
 *
 * @author bajdcc
 */
public interface ISyntaxToken {

	/**
	 * ���ؼǺ�����
	 */
	public int getTokenID();

	/**
	 * �������Ƿ���Ч
	 */
	public boolean available();

	/**
	 * ��ǰ
	 */
	public void previous();

	/**
	 * ���
	 */
	public void next();

	/**
	 * ���ؼǺ�����λ��
	 */
	public int getPosition();
}
