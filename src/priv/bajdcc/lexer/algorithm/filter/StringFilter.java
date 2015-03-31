package priv.bajdcc.lexer.algorithm.filter;

import priv.bajdcc.lexer.error.RegexException;
import priv.bajdcc.lexer.error.RegexException.RegexError;
import priv.bajdcc.lexer.regex.IRegexStringFilter;
import priv.bajdcc.lexer.regex.IRegexStringFilterMeta;
import priv.bajdcc.lexer.regex.IRegexStringIterator;
import priv.bajdcc.lexer.regex.RegexStringIteratorData;
import priv.bajdcc.lexer.regex.RegexStringUtility;
import priv.bajdcc.lexer.token.MetaType;

/**
 * 字符串类型过滤
 * 
 * @author bajdcc
 *
 */
public class StringFilter implements IRegexStringFilter, IRegexStringFilterMeta {

	/**
	 * 字符串首尾的终结符
	 */
	private MetaType m_kMeta = MetaType.NULL;
	
	public StringFilter(MetaType meta) {
		m_kMeta = meta;
	}

	@Override
	public RegexStringIteratorData filter(IRegexStringIterator iterator) {
		RegexStringUtility utility = iterator.utility();// 获取解析组件
		RegexStringIteratorData data = new RegexStringIteratorData();
		try {
			if (!iterator.available()) {
				data.m_kMeta = MetaType.END;
				data.m_chCurrent = MetaType.END.getChar();
			} else {
				data.m_kMeta = iterator.meta();
				data.m_chCurrent = iterator.current();
				iterator.next();
				if (data.m_kMeta == m_kMeta) {// 过滤终结符
					data.m_kMeta = MetaType.NULL;
				} else if (data.m_kMeta == MetaType.ESCAPE) {// 处理转义
					data.m_chCurrent = iterator.current();
					iterator.next();
					data.m_kMeta = MetaType.MUST_SAVE;
					data.m_chCurrent = utility.fromEscape(data.m_chCurrent,
							RegexError.ESCAPE);
				}
			}
		} catch (RegexException e) {
			System.err.println(e.getPosition() + " : "
					+ e.getMessage());
			data.m_kMeta = MetaType.ERROR;
			data.m_chCurrent = MetaType.ERROR.getChar();
		}
		return data;
	}

	@Override
	public IRegexStringFilterMeta getFilterMeta() {
		return this;
	}

	@Override
	public MetaType[] getMetaTypes() {
		return new MetaType[] { m_kMeta, MetaType.ESCAPE };
	}
}
