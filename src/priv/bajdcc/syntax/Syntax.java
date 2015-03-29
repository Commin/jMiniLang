package priv.bajdcc.syntax;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import priv.bajdcc.lexer.error.RegexException;
import priv.bajdcc.syntax.automata.npa.NPA;
import priv.bajdcc.syntax.error.IErrorHandler;
import priv.bajdcc.syntax.error.SyntaxException;
import priv.bajdcc.syntax.error.SyntaxException.SyntaxError;
import priv.bajdcc.syntax.exp.BranchExp;
import priv.bajdcc.syntax.exp.IExpCollction;
import priv.bajdcc.syntax.exp.OptionExp;
import priv.bajdcc.syntax.exp.PropertyExp;
import priv.bajdcc.syntax.exp.RuleExp;
import priv.bajdcc.syntax.exp.SequenceExp;
import priv.bajdcc.syntax.exp.TokenExp;
import priv.bajdcc.syntax.lexer.SyntaxLexer;
import priv.bajdcc.syntax.solver.FirstsetSolver;
import priv.bajdcc.syntax.stringify.SyntaxToString;
import priv.bajdcc.syntax.token.OperatorType;
import priv.bajdcc.syntax.token.Token;
import priv.bajdcc.syntax.token.TokenType;
import priv.bajdcc.utility.BitVector2;
import priv.bajdcc.utility.Position;

/**
 * �ķ�������
 * 
 * �﷨ʾ���� Z -> A | B | `abc` | ( A | `c`<terminal comment text> | C<comment> |
 * C{Error handler name})
 *
 * @author bajdcc
 */
public class Syntax {

	/**
	 * �ս����
	 */
	private ArrayList<TokenExp> m_arrTerminals = new ArrayList<TokenExp>();

	/**
	 * �ս����
	 */
	private HashMap<String, TokenExp> m_mapTerminals = new HashMap<String, TokenExp>();

	/**
	 * ���ս����
	 */
	private ArrayList<RuleExp> m_arrNonTerminals = new ArrayList<RuleExp>();

	/**
	 * ���ս����
	 */
	private HashMap<String, RuleExp> m_mapNonTerminals = new HashMap<String, RuleExp>();

	/**
	 * ���Ա�
	 */
	private ArrayList<PropertyExp> m_arrProperties = new ArrayList<PropertyExp>();
	/**
	 * ���Ա�
	 */
	private HashMap<String, PropertyExp> m_mapProperties = new HashMap<String, PropertyExp>();

	/**
	 * �ķ���ʼ����
	 */
	private String m_strBeginRuleName = "";

	/**
	 * �����ķ��Ĵʷ�������
	 */
	private SyntaxLexer m_SyntaxLexer = new SyntaxLexer();

	/**
	 * ��ǰ�ַ�
	 */
	private Token m_Token = null;

	/**
	 * ��ǰ�������ķ�����
	 */
	private RuleExp m_Rule = null;

	/**
	 * ��ȷ���������Զ���
	 */
	private NPA m_NPA = null;

	public Syntax() throws RegexException {
		this(true);
	}

	public Syntax(boolean ignoreLexError) throws RegexException {
		m_SyntaxLexer.discard(TokenType.COMMENT);
		m_SyntaxLexer.discard(TokenType.WHITSPACE);
		if (ignoreLexError) {
			m_SyntaxLexer.discard(TokenType.ERROR);
		}
	}

	/**
	 * ����ս��
	 * 
	 * @param name
	 *            �ս������
	 * @param regex
	 *            �ս����Ӧ��������ʽ
	 */
	public void addTerminal(String name, String regex) {
		TokenExp exp = new TokenExp(m_arrTerminals.size(), name, regex);
		if (m_mapTerminals.put(name, exp) == null) {
			m_arrTerminals.add(exp);
		}
	}

	/**
	 * ��ӷ��ս��
	 * 
	 * @param name
	 *            ���ս������
	 */
	public void addNonTerminal(String name) {
		RuleExp exp = new RuleExp(m_arrNonTerminals.size(), name);
		if (m_mapNonTerminals.put(name, exp) == null) {
			m_arrNonTerminals.add(exp);
		}
	}

	/**
	 * ��Ӵ�������
	 * 
	 * @param name
	 *            ��������
	 * @param handler
	 *            ����ӿ�
	 */
	public void addErrorHandler(String name, IErrorHandler handler) {
		PropertyExp exp = new PropertyExp(m_mapProperties.size(), handler);
		if (m_mapProperties.put(name, exp) == null) {
			m_arrProperties.add(exp);
		}
	}

	/**
	 * @param inferString
	 *            �ķ��Ƶ�ʽ
	 * @throws SyntaxException
	 */
	public void infer(String inferString) throws SyntaxException {
		m_SyntaxLexer.setContext(inferString);
		compile();
	}

	/**
	 * �׳��쳣
	 * 
	 * @param error
	 *            ��������
	 * @throws SyntaxException
	 */
	private void err(SyntaxError error) throws SyntaxException {
		throw new SyntaxException(error, m_SyntaxLexer.position(),
				m_Token.m_Object);
	}

	/**
	 * �׳��쳣
	 * 
	 * @param error
	 *            ��������
	 * @param obj
	 *            ������Ϣ
	 * @throws SyntaxException
	 */
	private void err(SyntaxError error, Object obj) throws SyntaxException {
		throw new SyntaxException(error, new Position(), obj);
	}

	/**
	 * ƥ�����
	 * 
	 * @param type
	 *            ƥ������
	 * @param error
	 *            ��������
	 * @throws SyntaxException
	 */
	private void expect(TokenType type, SyntaxError error)
			throws SyntaxException {
		if (m_Token.m_kToken == type) {
			next();
		} else {
			err(error);
		}
	}

	/**
	 * ��ȷƥ�䵱ǰ�ַ�
	 * 
	 * @param type
	 *            ƥ������
	 * @param error
	 *            ƥ��ʧ��ʱ�׳����쳣
	 * @throws SyntaxException
	 */
	private void match(TokenType type, SyntaxError error)
			throws SyntaxException {
		if (m_Token.m_kToken != type) {
			err(error);
		}
	}

	/**
	 * ƥ����ս��
	 * 
	 * @throws SyntaxException
	 */
	private RuleExp matchNonTerminal() throws SyntaxException {
		match(TokenType.NONTERMINAL, SyntaxError.SYNTAX);
		if (!m_mapNonTerminals.containsKey(m_Token.m_Object.toString())) {
			err(SyntaxError.UNDECLARED);
		}
		return m_mapNonTerminals.get(m_Token.m_Object.toString());
	}

	/**
	 * ƥ���ս��
	 * 
	 * @throws SyntaxException
	 */
	private TokenExp matchTerminal() throws SyntaxException {
		match(TokenType.TERMINAL, SyntaxError.SYNTAX);
		if (!m_mapTerminals.containsKey(m_Token.m_Object.toString())) {
			err(SyntaxError.UNDECLARED);
		}
		return m_mapTerminals.get(m_Token.m_Object.toString());
	}

	/**
	 * ƥ�����Ա�
	 * 
	 * @throws SyntaxException
	 */
	private PropertyExp matchProperty() throws SyntaxException {
		match(TokenType.HANDLER, SyntaxError.SYNTAX);
		if (!m_mapProperties.containsKey(m_Token.m_Object.toString())) {
			err(SyntaxError.UNDECLARED);
		}
		return m_mapProperties.get(m_Token.m_Object.toString());
	}

	/**
	 * ȡ��һ������
	 */
	private Token next() {
		m_Token = m_SyntaxLexer.scan();
		return m_Token == null ? next() : m_Token;
	}

	/**
	 * �����Ƶ�ʽ�����ı����ʽת�����ķ�����
	 * 
	 * @throws SyntaxException
	 */
	private void compile() throws SyntaxException {
		/* ������˷��ս�� */
		doHead();
		/* �����Ҷ˱��ʽ */
		doTail();
	}

	/**
	 * ������˷��ս��
	 * 
	 * @throws SyntaxException
	 */
	private void doHead() throws SyntaxException {
		/* ƥ���Ƶ�ʽ��˵ķ��ս�� */
		next();
		matchNonTerminal();
		/* �趨������Ҫ�Ƶ��ķ��ս�� */
		m_Rule = m_mapNonTerminals.get(m_Token.m_Object.toString());
		/* ƥ���Ƶ�����"->" */
		next();
		expect(TokenType.OPERATOR, SyntaxError.SYNTAX);
	}

	/**
	 * �����Ҷ˱��ʽ
	 * 
	 * @throws SyntaxException
	 */
	private void doTail() throws SyntaxException {
		/* ��÷�����ı��ʽ����� */
		ISyntaxComponent exp = doAnalysis(TokenType.EOF, null);
		/* ���������ӽ���Ӧ���� */
		m_Rule.m_Rule.m_arrRules.add(new RuleItem(exp, m_Rule.m_Rule));
	}

	/**
	 * �������ʽ
	 * 
	 * @param type
	 *            ��������
	 * @param obj
	 *            ��������
	 * @return ���ʽ�������
	 * @throws SyntaxException
	 */
	private ISyntaxComponent doAnalysis(TokenType type, Object obj)
			throws SyntaxException {

		/* �½��������ڴ�Ž�� */
		SequenceExp sequence = new SequenceExp();
		/* ���ܻ�ʹ�õķ�֧ */
		BranchExp branch = null;
		/* ����ӽ��ӿ� */
		IExpCollction collection = sequence;
		/* ���ʽͨ�ýӿ� */
		ISyntaxComponent result = sequence;

		for (;;) {
			if ((m_Token.m_kToken == type && (m_Token.m_Object == null || m_Token.m_Object
					.equals(obj)))) {// �����ַ�
				if (m_SyntaxLexer.index() == 0) {// ���ʽΪ��
					err(SyntaxError.NULL);
				} else if (collection.isEmpty()) {// ����Ϊ��
					err(SyntaxError.INCOMPLETE);
				} else {
					next();
					break;// ������ֹ
				}
			} else if (m_Token.m_kToken == TokenType.EOF) {
				err(SyntaxError.INCOMPLETE);
			}
			ISyntaxComponent exp = null;// ��ǰ����ֵ�ı��ʽ
			switch (m_Token.m_kToken) {
			case OPERATOR:
				OperatorType op = (OperatorType) m_Token.m_Object;
				next();
				switch (op) {
				case ALTERNATIVE:
					if (collection.isEmpty())// �ڴ�֮ǰû�д洢���ʽ (|...)
					{
						err(SyntaxError.INCOMPLETE);
					} else {
						if (branch == null) {// ��֧Ϊ�գ�������֧
							branch = new BranchExp();
							branch.add(sequence);// ���½��ķ�֧�����������ǰ����
							result = branch;
						}
						sequence = new SequenceExp();// �½�һ������
						branch.add(sequence);
						continue;
					}
					break;
				case LPARAN:// '('
					exp = doAnalysis(TokenType.OPERATOR, OperatorType.RPARAN);// �ݹ����
					break;
				case LSQUARE:// '['
					exp = doAnalysis(TokenType.OPERATOR, OperatorType.RSQUARE);// �ݹ����
					OptionExp option = new OptionExp();// ��װ
					option.m_Expression = exp;
					exp = option;
					break;
				default:
					err(SyntaxError.SYNTAX);
					break;
				}
				break;
			case EOF:
				return result;
			case TERMINAL:
				exp = matchTerminal();
				next();
				break;
			case NONTERMINAL:
				RuleExp rule = matchNonTerminal();
				next();
				if (m_Token.m_kToken == TokenType.HANDLER) {
					PropertyExp property = matchProperty();
					property.m_Expression = rule;
					exp = property;
					next();
				} else {
					exp = rule;
				}
				break;
			default:
				err(SyntaxError.SYNTAX);
				break;
			}

			if (exp != null) {
				sequence.add(exp);
			}
		}
		return result;
	}

	/**
	 * ��ʼ��
	 * 
	 * @param startSymbol
	 *            ��ʼ����
	 * @throws SyntaxException
	 */
	public void initialize(String startSymbol) throws SyntaxException {
		m_strBeginRuleName = startSymbol;
		checkStartSymbol();
		semanticAnalysis();
		generateNGA();
	}

	/**
	 * �����ʼ���źϷ���
	 * 
	 * @throws SyntaxException
	 */
	private void checkStartSymbol() throws SyntaxException {
		if (!m_mapNonTerminals.containsKey(m_strBeginRuleName)) {
			err(SyntaxError.UNDECLARED);
		}
	}

	/**
	 * �����������
	 * 
	 * @throws SyntaxException
	 */
	private void semanticAnalysis() throws SyntaxException {
		/* ���ս������ */
		int size = m_arrNonTerminals.size();
		/* ��������First���� */
		for (RuleExp exp : m_arrNonTerminals) {
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				FirstsetSolver solver = new FirstsetSolver();
				item.m_Expression.visit(solver);// ��������First����
				if (!solver.solve(item)) {// �������ȿ���Ϊ�㣬�������մ�
					err(SyntaxError.EPSILON,
							getSingleString(exp.m_strName, item.m_Expression));
				}
			}
		}
		/* ������ͨ���� */
		BitVector2 firstsetDependency = new BitVector2(size, size);// First����������
		firstsetDependency.clear();
		/* ������ս��First���ϰ�����ϵ�Ĳ�����ͨ���� */
		{
			int i = 0;
			for (RuleExp exp : m_arrNonTerminals) {
				for (RuleItem item : exp.m_Rule.m_arrRules) {
					for (RuleExp rule : item.m_arrFirstSetRules) {
						firstsetDependency.set(i, rule.m_iID);
					}
				}
				i++;
			}
		}
		/* ��������ݹ� */
		{
			/* ��ǲ����ֱ����ݹ� */
			for (int i = 0; i < size; i++) {
				if (firstsetDependency.test(i, i)) {
					m_arrNonTerminals.get(i).m_Rule.m_iRecursiveLevel = 1;// ֱ����ݹ�
					firstsetDependency.set(i, i);
				}
			}
			/* ��ÿ��� */
			BitVector2 a = (BitVector2) firstsetDependency.clone();
			BitVector2 b = (BitVector2) firstsetDependency.clone();
			BitVector2 r = new BitVector2(size, size);
			/* ����Ƿ���ֻ� */
			for (int level = 2; level < size; level++) {// ***
														// Warshell�㷨��������ͼ�Ĵ��ݱհ�
														// ***
				/* ���в�����ͨ����˷�����r=aXb */
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						r.clear(i, j);
						for (int k = 0; k < size; k++) {
							boolean value = r.test(i, j)
									|| (a.test(i, k) && b.test(k, j));
							r.set(i, j, value);
						}
					}
				}
				/* ��鵱ǰ����Ƿ���ֻ� */
				{
					int i = 0;
					for (RuleExp exp : m_arrNonTerminals) {
						if (r.test(i, i)) {
							if (exp.m_Rule.m_iRecursiveLevel < 2) {
								exp.m_Rule.m_iRecursiveLevel = level;
							}
						}
						i++;
					}
				}
				/* ������ */
				a = (BitVector2) r.clone();
			}
			/* ����Ƿ���ڻ���������� */
			for (RuleExp exp : m_arrNonTerminals) {
				if (exp.m_Rule.m_iRecursiveLevel > 1) {
					err(SyntaxError.INDIRECT_RECURSION, exp.m_strName
							+ " level:" + exp.m_Rule.m_iRecursiveLevel);
				}
			}
		}
		/* ����������First���� */
		{
			/* ���������Ǳ� */
			BitSet processed = new BitSet(size);
			processed.clear();
			for (int i = 0; i < size; i++) {
				/* �ҳ�һ�������������Ĺ��� */
				int nodependencyRule = -1;// ���������Ĺ�������
				for (int j = 0; j < size; j++) {
					if (!processed.get(j)) {
						boolean empty = true;
						for (int k = 0; k < size; k++) {
							if (firstsetDependency.test(j, k)) {
								empty = false;
								break;
							}
						}
						if (empty) {// �ҵ�
							nodependencyRule = j;
							break;
						}
					}
				}
				/* ����ù�����ս��First���� */
				{
					Rule rule = m_arrNonTerminals.get(nodependencyRule).m_Rule;
					/* ���������ս��First���� */
					for (RuleItem item : rule.m_arrRules) {
						for (RuleExp exp : item.m_arrFirstSetRules) {
							item.m_arrFirstSetTokens
									.addAll(exp.m_Rule.m_arrTokens);
						}
					}
					/* ������ս�����ս��First���� */
					for (RuleItem item : rule.m_arrRules) {
						rule.m_arrTokens.addAll(item.m_arrFirstSetTokens);
					}
					/* ������ݹ������ս��First���� */
					for (RuleItem item : rule.m_arrRules) {
						if (item.m_arrFirstSetRules.contains(m_arrNonTerminals
								.get(nodependencyRule))) {
							item.m_arrFirstSetTokens.addAll(rule.m_arrTokens);
						}
					}
				}
				/* ����ù��� */
				processed.set(nodependencyRule);
				for (int j = 0; j < size; j++) {
					firstsetDependency.clear(j, nodependencyRule);
				}
			}
		}
		/* �������ܲ����ַ����Ĺ��� */
		for (RuleExp exp : m_arrNonTerminals) {
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				if (item.m_arrFirstSetTokens.isEmpty()) {
					err(SyntaxError.FAILED,
							getSingleString(exp.m_strName, item.m_Expression));
				}
			}
		}
	}

	/**
	 * ���ɷ�ȷ�����ķ��Զ���
	 */
	private void generateNGA() {
		m_NPA = new NPA(m_arrNonTerminals);
	}

	/**
	 * ��õ�һ����ʽ����
	 * 
	 * @param name
	 *            ���ս������
	 * @param exp
	 *            ���ʽ��
	 * @param focused
	 *            ����
	 * @param front
	 *            ǰ��
	 * @return ԭ����ʽ����
	 */
	public static String getSingleString(String name, ISyntaxComponent exp,
			ISyntaxComponent focused, boolean front) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(" -> ");
		SyntaxToString alg = new SyntaxToString(focused, front);
		exp.visit(alg);
		sb.append(alg.toString());
		return sb.toString();
	}

	/**
	 * ��õ�һ����ʽ����
	 * 
	 * @param name
	 *            ���ս������
	 * @param exp
	 *            ���ʽ��
	 * @return ԭ����ʽ����
	 */
	public static String getSingleString(String name, ISyntaxComponent exp) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(" -> ");
		SyntaxToString alg = new SyntaxToString();
		exp.visit(alg);
		sb.append(alg.toString());
		return sb.toString();
	}

	/**
	 * ��ö���ʽ����
	 */
	public String getParagraphString() {
		StringBuffer sb = new StringBuffer();
		/* ��ʼ���� */
		sb.append("#### ��ʼ���� ####");
		sb.append(System.getProperty("line.separator"));
		sb.append(m_strBeginRuleName);
		sb.append(System.getProperty("line.separator"));
		/* �ս�� */
		sb.append("#### �ս�� ####");
		sb.append(System.getProperty("line.separator"));
		for (TokenExp exp : m_arrTerminals) {
			sb.append(exp.toString());
			sb.append(System.getProperty("line.separator"));
		}
		/* ���ս�� */
		sb.append("#### ���ս�� ####");
		sb.append(System.getProperty("line.separator"));
		for (RuleExp exp : m_arrNonTerminals) {
			sb.append(exp.toString());
			sb.append(System.getProperty("line.separator"));
		}
		/* �Ƶ����� */
		sb.append("#### �ķ�����ʽ ####");
		sb.append(System.getProperty("line.separator"));
		for (RuleExp exp : m_arrNonTerminals) {
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				/* �������� */
				sb.append(getSingleString(exp.m_strName, item.m_Expression));
				sb.append(System.getProperty("line.separator"));
				/* First���� */
				sb.append("\t--== �ս��First���� ==--");
				sb.append(System.getProperty("line.separator"));
				for (TokenExp token : item.m_arrFirstSetTokens) {
					sb.append("\t\t" + token.m_strName);
					sb.append(System.getProperty("line.separator"));
				}
				sb.append("\t--== ���ս��First���� ==--");
				sb.append(System.getProperty("line.separator"));
				for (RuleExp rule : item.m_arrFirstSetRules) {
					sb.append("\t\t" + rule.m_strName);
					sb.append(System.getProperty("line.separator"));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * ���ԭ�Ƶ�ʽ����
	 */
	public String getOriginalString() {
		StringBuffer sb = new StringBuffer();
		for (RuleExp exp : m_arrNonTerminals) {
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				sb.append(getSingleString(exp.m_strName, item.m_Expression));
				sb.append(System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}

	/**
	 * ��÷�ȷ�����ķ��Զ�������
	 */
	public String getNGAString() {
		return m_NPA.getNGAString();
	}

	@Override
	public String toString() {
		return getParagraphString();
	}
}
