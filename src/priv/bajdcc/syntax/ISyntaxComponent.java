package priv.bajdcc.syntax;

/**
 * �ķ������ӿڣ�������ʽ���ࣩ
 * 
 * @author bajdcc
 */
public interface ISyntaxComponent {
	/**
	 * �趨������ʽ
	 * 
	 * @param visitor
	 *            �ݹ�����㷨
	 */
	public void visit(ISyntaxComponentVisitor visitor);
}
