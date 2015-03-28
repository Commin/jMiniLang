package priv.bajdcc.syntax.exp;

import priv.bajdcc.syntax.ISyntaxComponent;
import priv.bajdcc.syntax.ISyntaxComponentVisitor;
import priv.bajdcc.syntax.Rule;
import priv.bajdcc.utility.VisitBag;

/**
 * �ķ����򣨷��ս����
 *
 * @author bajdcc
 */
public class RuleExp implements ISyntaxComponent {

	/**
	 * ���ս��ID
	 */
	public int m_iID = -1;

	/**
	 * ���ս������
	 */
	public String m_strName = null;

	/**
	 * ����
	 */
	public Rule m_Rule = new Rule(this);

	public RuleExp(int id, String name) {
		m_iID = id;
		m_strName = name;
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
