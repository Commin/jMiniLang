package priv.bajdcc.syntax;

import java.util.ArrayList;
import java.util.HashMap;

import priv.bajdcc.lexer.error.RegexException;
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
import priv.bajdcc.syntax.exp.stringify.SyntaxToString;
import priv.bajdcc.syntax.lexer.SyntaxLexer;
import priv.bajdcc.syntax.token.OperatorType;
import priv.bajdcc.syntax.token.Token;
import priv.bajdcc.syntax.token.TokenType;

/**
 * �ķ�������
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
	 * @param token
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (RuleExp exp : m_arrNonTerminals) {
			for (RuleItem item : exp.m_Rule.m_arrRules) {
				sb.append(exp.m_strName);
				sb.append(" -> ");
				SyntaxToString alg = new SyntaxToString();
				item.m_Expression.visit(alg);
				sb.append(alg.toString());
				sb.append(System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}
}
