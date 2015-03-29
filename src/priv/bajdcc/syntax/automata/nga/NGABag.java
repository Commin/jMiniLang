package priv.bajdcc.syntax.automata.nga;

import java.util.ArrayList;
import java.util.Stack;

import priv.bajdcc.syntax.ISyntaxComponent;

/**
 * ��ȷ�����ķ��Զ��������
 *
 * @author bajdcc
 */
public class NGABag {
	/**
	 * NGAջ
	 */
	public Stack<ArrayList<ENGA>> m_stkNGA = new Stack<ArrayList<ENGA>>();

	/**
	 * NGA�ӱ�
	 */
	public ArrayList<ENGA> m_childNGA = new ArrayList<ENGA>();

	/**
	 * �洢�����ENGA
	 */
	public ENGA m_outputNGA = null;

	/**
	 * ���ǰ׺
	 */
	public String m_strPrefix = "";

	/**
	 * ���ǰ׺
	 */
	public ISyntaxComponent m_Expression = null;
}
