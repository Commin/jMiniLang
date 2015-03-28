package priv.bajdcc.syntax.lexer;

import java.util.HashSet;

import priv.bajdcc.lexer.algorithm.ITokenAlgorithm;
import priv.bajdcc.lexer.algorithm.TokenAlgorithmCollection;
import priv.bajdcc.lexer.algorithm.impl.WhitespaceTokenizer;
import priv.bajdcc.lexer.error.RegexException;
import priv.bajdcc.lexer.regex.IRegexStringFilterHost;
import priv.bajdcc.lexer.regex.RegexStringIterator;
import priv.bajdcc.lexer.token.MetaType;
import priv.bajdcc.syntax.lexer.tokenizer.CommentTokenizer;
import priv.bajdcc.syntax.lexer.tokenizer.NonTerminalTokenizer;
import priv.bajdcc.syntax.lexer.tokenizer.OperatorTokenizer;
import priv.bajdcc.syntax.lexer.tokenizer.TerminalTokenizer;
import priv.bajdcc.syntax.token.Token;
import priv.bajdcc.syntax.token.TokenType;

/**
 * �����ķ��Ĵʷ�������
 * 
 * @author bajdcc
 */
public class SyntaxLexer extends RegexStringIterator implements
		IRegexStringFilterHost {

	/**
	 * �㷨���ϣ�������ʽƥ�䣩
	 */
	private TokenAlgorithmCollection m_algCollections = new TokenAlgorithmCollection(
			this, this);

	/**
	 * �ַ�ת���㷨
	 */
	private ITokenAlgorithm m_TokenAlg = null;

	/**
	 * ���������ͼ���
	 */
	private HashSet<TokenType> m_setDiscardToken = new HashSet<TokenType>();

	public SyntaxLexer() throws RegexException {
		initialize();
	}

	/**
	 * ����Ҫ����������
	 * 
	 * @param context
	 *            �ķ��Ƶ�ʽ
	 */
	public void setContext(String context) {
		/* ��ʼ�� */
		m_strContext = context;
		m_Position.m_iColumn = 0;
		m_Position.m_iLine = 0;
		m_Data.m_chCurrent = 0;
		m_Data.m_iIndex = 0;
		m_Data.m_kMeta = MetaType.END;
		m_stkIndex.clear();
		m_stkPosition.clear();
	}

	/**
	 * ��ȡһ������
	 * 
	 * @return ����
	 */
	public Token scan() {
		Token token = Token.transfer(m_algCollections.scan());
		if (m_setDiscardToken.contains(token.m_kToken)) {// ��Ҫ����
			return null;
		}
		return token;
	}

	/**
	 * ���ö�������
	 * 
	 * @param type
	 *            Ҫ�����ķ������ͣ������鶪��EOF����Ϊ��Ҫ�����жϽ�����
	 */
	public void discard(TokenType type) {
		m_setDiscardToken.add(type);
	}

	@Override
	public void setFilter(ITokenAlgorithm alg) {
		m_TokenAlg = alg;
	}

	@Override
	protected void transform() {
		super.transform();
		if (m_TokenAlg != null) {
			m_Data.m_kMeta = m_TokenAlg.getMetaHash().get(m_Data.m_chCurrent);
		}
	}

	/**
	 * ��ʼ������������
	 * 
	 * @throws RegexException
	 */
	private void initialize() throws RegexException {
		//
		// ### �㷨������װ�ؽ����������һ��˳��� ###
		//
		// �������ԭ��
		// ÿ��������Լ���������ʽƥ���ַ���
		// ����ѡ�����Լ��Ĺ��˷��������ַ����е�ת�����
		//
		// ����ʱ���ֱ�����ý�����������������ʧ�ܣ��������һ���
		// ��ĳһ��������ɹ���������ƥ����
		// ��ȫ������ʧ�ܣ�����ó�����Ĭ��Ϊǰ��һ�ַ���
		//
		m_algCollections.attach(new WhitespaceTokenizer());// �հ��ַ��������
		m_algCollections.attach(new CommentTokenizer());// ע�ͽ������
		m_algCollections.attach(new TerminalTokenizer());// �ս���������
		m_algCollections.attach(new NonTerminalTokenizer());// ���ս���������
		m_algCollections.attach(new OperatorTokenizer());// �������������
	}
}
