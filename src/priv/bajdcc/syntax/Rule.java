package priv.bajdcc.syntax;

import java.util.ArrayList;

import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.TokenExp;

/**
 * �ķ������ķ��Ƶ�ʽ��
 *
 * @author bajdcc
 */
public class Rule {

	/**
	 * ������ʽ�б�
	 */
	public ArrayList<RuleItem> m_arrRules = new ArrayList<RuleItem>();

	/**
	 * ������ʼ���ս��
	 */
	public RuleExp m_nonTerminal = null;

	/**
	 * ��ݹ�ȼ���0Ϊ��1Ϊֱ�ӣ�����1Ϊ���
	 */
	public int m_iRecursiveLevel = 0;

	/**
	 * �ս��First����
	 */
	public ArrayList<TokenExp> m_arrTokens = new ArrayList<TokenExp>();

	public Rule(RuleExp exp) {
		m_nonTerminal = exp;
	}
}
