package priv.bajdcc.syntax.exp;

import priv.bajdcc.syntax.ISyntaxComponent;
import priv.bajdcc.syntax.ISyntaxComponentVisitor;
import priv.bajdcc.utility.VisitBag;

/**
 * �ķ������ս����
 *
 * @author bajdcc
 */
public class TokenExp implements ISyntaxComponent {

	/**
	 * �ս��ID
	 */
	public int m_iID = -1;

	/**
	 * �ս������
	 */
	public String m_strName = null;

	/**
	 * �ս����Ӧ��������ʽ
	 */
	public String m_strRegex = null;

	public TokenExp(int id, String name, String regex) {
		m_iID = id;
		m_strName = name;
		m_strRegex = regex;
	}

	@Override
	public void visit(ISyntaxComponentVisitor visitor) {
		VisitBag bag = new VisitBag();
		visitor.visitBegin(this, bag);
		if (bag.m_bVisitEnd) {
			visitor.visitEnd(this);
		}
	}
}
