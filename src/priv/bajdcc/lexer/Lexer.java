package priv.bajdcc.lexer;

import java.util.HashSet;

import priv.bajdcc.lexer.algorithm.ITokenAlgorithm;
import priv.bajdcc.lexer.algorithm.TokenAlgorithmCollection;
import priv.bajdcc.lexer.algorithm.impl.CharacterTokenizer;
import priv.bajdcc.lexer.algorithm.impl.CommentTokenizer;
import priv.bajdcc.lexer.algorithm.impl.IdentifierTokenizer;
import priv.bajdcc.lexer.algorithm.impl.KeywordTokenizer;
import priv.bajdcc.lexer.algorithm.impl.MacroTokenizer;
import priv.bajdcc.lexer.algorithm.impl.NumberTokenizer;
import priv.bajdcc.lexer.algorithm.impl.OperatorTokenizer;
import priv.bajdcc.lexer.algorithm.impl.StringTokenizer;
import priv.bajdcc.lexer.algorithm.impl.WhitespaceTokenizer;
import priv.bajdcc.lexer.error.RegexException;
import priv.bajdcc.lexer.regex.IRegexStringFilterHost;
import priv.bajdcc.lexer.regex.RegexStringIterator;
import priv.bajdcc.lexer.token.Token;
import priv.bajdcc.lexer.token.TokenType;

/**
 * �ʷ�������
 * 
 * @author bajdcc
 */
public class Lexer extends RegexStringIterator implements
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

	public Lexer(String context) throws RegexException {
		super(context);
		initialize();
	}

	/**
	 * ��ȡһ������
	 * 
	 * @return ����
	 */
	public Token scan() {
		Token token = m_algCollections.scan();
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
		m_algCollections.attach(new MacroTokenizer());// ��������
		m_algCollections.attach(new StringTokenizer());// �ַ����������
		m_algCollections.attach(new CharacterTokenizer());// �ַ��������
		m_algCollections.attach(new IdentifierTokenizer());// ��ʶ���������
		m_algCollections.attach(new KeywordTokenizer());// �ؼ��ֽ������
		m_algCollections.attach(new NumberTokenizer());// ���ֽ������
		m_algCollections.attach(new OperatorTokenizer());// �������������
	}
}
