package priv.bajdcc.syntax.exp;

import priv.bajdcc.syntax.ISyntaxComponent;

/**
 * ������Ӻ��ӽ��ı��ʽ�ӿ�
 *
 * @author bajdcc
 */
public interface IExpCollction {
	/**
	 * ��Ӻ��ӽ��
	 * 
	 * @param exp
	 *            �ӱ��ʽ
	 */
	public void add(ISyntaxComponent exp);
	
	/**
	 * �����Ƿ�Ϊ��
	 */
	public boolean isEmpty();
}
