// Generated from EclRecord.g4 by ANTLR 4.8
package org.hpccsystems.ws.client.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class EclRecordParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, OPAREN=16, 
		CPAREN=17, OCURLY=18, CCURLY=19, COMMA=20, SEMI=21, EQ=22, ASSING_SYM=23, 
		REC_SYM=24, END_SYM=25, DATASET_SYM=26, WS=27, INT=28, STRING=29, ATOKEN=30, 
		TOKEN=31, UTOKEN=32, ECL_NUMBERED_TYPE=33;
	public static final int
		RULE_program = 0, RULE_value = 1, RULE_value_list = 2, RULE_token_list = 3, 
		RULE_assign = 4, RULE_assign_list = 5, RULE_eclfield_decl = 6, RULE_eclfield_type = 7, 
		RULE_eclfield_name = 8, RULE_eclfield_recref = 9, RULE_payload_sep = 10, 
		RULE_record_def_inline = 11, RULE_record_def = 12, RULE_defined_record_def = 13, 
		RULE_exploded_dataset_record_def = 14, RULE_inline_dataset_record_def = 15, 
		RULE_record_defs = 16, RULE_nested_dataset_decl = 17, RULE_nested_inline_dataset_decl = 18, 
		RULE_opts = 19, RULE_opt = 20, RULE_maxlength = 21, RULE_blob = 22, RULE_maxcount = 23, 
		RULE_defaultval = 24, RULE_xpath = 25, RULE_xmldefaultval = 26, RULE_annotation_name = 27, 
		RULE_annotation_param = 28, RULE_annotation_arguments = 29, RULE_annotation = 30, 
		RULE_comment = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "value", "value_list", "token_list", "assign", "assign_list", 
			"eclfield_decl", "eclfield_type", "eclfield_name", "eclfield_recref", 
			"payload_sep", "record_def_inline", "record_def", "defined_record_def", 
			"exploded_dataset_record_def", "inline_dataset_record_def", "record_defs", 
			"nested_dataset_decl", "nested_inline_dataset_decl", "opts", "opt", "maxlength", 
			"blob", "maxcount", "defaultval", "xpath", "xmldefaultval", "annotation_name", 
			"annotation_param", "annotation_arguments", "annotation", "comment"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'SET OF'", "'set of'", "'MAXLENGTH'", "'maxlength'", "'=>'", "'maxLength'", 
			"'BLOB'", "'blob'", "'MAXCOUNT'", "'DEFAULT'", "'XPATH'", "'XMLDEFAULT'", 
			"'//'", "'/*'", "'*/'", "'('", "')'", "'{'", "'}'", "','", null, "'='", 
			"':='", "'RECORD'", "'END'", "'DATASET'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "OPAREN", "CPAREN", "OCURLY", "CCURLY", "COMMA", 
			"SEMI", "EQ", "ASSING_SYM", "REC_SYM", "END_SYM", "DATASET_SYM", "WS", 
			"INT", "STRING", "ATOKEN", "TOKEN", "UTOKEN", "ECL_NUMBERED_TYPE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "EclRecord.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EclRecordParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public List<Record_defsContext> record_defs() {
			return getRuleContexts(Record_defsContext.class);
		}
		public Record_defsContext record_defs(int i) {
			return getRuleContext(Record_defsContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			record_defs();
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OCURLY) | (1L << REC_SYM) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) {
				{
				{
				setState(65);
				record_defs();
				}
				}
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public TerminalNode INT() { return getToken(EclRecordParser.INT, 0); }
		public TerminalNode STRING() { return getToken(EclRecordParser.STRING, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << STRING) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_listContext extends ParserRuleContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public Value_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterValue_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitValue_list(this);
		}
	}

	public final Value_listContext value_list() throws RecognitionException {
		Value_listContext _localctx = new Value_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_value_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			value();
			setState(78);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(74);
					match(COMMA);
					setState(75);
					value();
					}
					} 
				}
				setState(80);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Token_listContext extends ParserRuleContext {
		public List<TerminalNode> TOKEN() { return getTokens(EclRecordParser.TOKEN); }
		public TerminalNode TOKEN(int i) {
			return getToken(EclRecordParser.TOKEN, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public Token_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_token_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterToken_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitToken_list(this);
		}
	}

	public final Token_listContext token_list() throws RecognitionException {
		Token_listContext _localctx = new Token_listContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_token_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(TOKEN);
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(82);
				match(COMMA);
				setState(83);
				match(TOKEN);
				}
				}
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode EQ() { return getToken(EclRecordParser.EQ, 0); }
		public Value_listContext value_list() {
			return getRuleContext(Value_listContext.class,0);
		}
		public AssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAssign(this);
		}
	}

	public final AssignContext assign() throws RecognitionException {
		AssignContext _localctx = new AssignContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_assign);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			value();
			setState(90);
			match(EQ);
			setState(91);
			value_list();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_listContext extends ParserRuleContext {
		public List<AssignContext> assign() {
			return getRuleContexts(AssignContext.class);
		}
		public AssignContext assign(int i) {
			return getRuleContext(AssignContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public Assign_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAssign_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAssign_list(this);
		}
	}

	public final Assign_listContext assign_list() throws RecognitionException {
		Assign_listContext _localctx = new Assign_listContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_assign_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			assign();
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(94);
				match(COMMA);
				setState(95);
				assign();
				}
				}
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eclfield_declContext extends ParserRuleContext {
		public Eclfield_typeContext eclfield_type() {
			return getRuleContext(Eclfield_typeContext.class,0);
		}
		public Eclfield_nameContext eclfield_name() {
			return getRuleContext(Eclfield_nameContext.class,0);
		}
		public Nested_dataset_declContext nested_dataset_decl() {
			return getRuleContext(Nested_dataset_declContext.class,0);
		}
		public Nested_inline_dataset_declContext nested_inline_dataset_decl() {
			return getRuleContext(Nested_inline_dataset_declContext.class,0);
		}
		public Eclfield_recrefContext eclfield_recref() {
			return getRuleContext(Eclfield_recrefContext.class,0);
		}
		public Inline_dataset_record_defContext inline_dataset_record_def() {
			return getRuleContext(Inline_dataset_record_defContext.class,0);
		}
		public TerminalNode OCURLY() { return getToken(EclRecordParser.OCURLY, 0); }
		public OptsContext opts() {
			return getRuleContext(OptsContext.class,0);
		}
		public TerminalNode CCURLY() { return getToken(EclRecordParser.CCURLY, 0); }
		public Eclfield_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclfield_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterEclfield_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitEclfield_decl(this);
		}
	}

	public final Eclfield_declContext eclfield_decl() throws RecognitionException {
		Eclfield_declContext _localctx = new Eclfield_declContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_eclfield_decl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(101);
				eclfield_type();
				setState(102);
				eclfield_name();
				setState(107);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
				case 1:
					{
					setState(103);
					match(OCURLY);
					setState(104);
					opts();
					setState(105);
					match(CCURLY);
					}
					break;
				}
				}
				break;
			case 2:
				{
				setState(109);
				nested_dataset_decl();
				}
				break;
			case 3:
				{
				setState(110);
				nested_inline_dataset_decl();
				}
				break;
			case 4:
				{
				{
				setState(111);
				inline_dataset_record_def();
				setState(112);
				eclfield_name();
				}
				}
				break;
			case 5:
				{
				setState(114);
				eclfield_recref();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eclfield_typeContext extends ParserRuleContext {
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public Eclfield_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclfield_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterEclfield_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitEclfield_type(this);
		}
	}

	public final Eclfield_typeContext eclfield_type() throws RecognitionException {
		Eclfield_typeContext _localctx = new Eclfield_typeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_eclfield_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0 || _la==T__1) {
				{
				setState(117);
				_la = _input.LA(1);
				if ( !(_la==T__0 || _la==T__1) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(120);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eclfield_nameContext extends ParserRuleContext {
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public Eclfield_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclfield_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterEclfield_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitEclfield_name(this);
		}
	}

	public final Eclfield_nameContext eclfield_name() throws RecognitionException {
		Eclfield_nameContext _localctx = new Eclfield_nameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_eclfield_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eclfield_recrefContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public Eclfield_recrefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eclfield_recref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterEclfield_recref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitEclfield_recref(this);
		}
	}

	public final Eclfield_recrefContext eclfield_recref() throws RecognitionException {
		Eclfield_recrefContext _localctx = new Eclfield_recrefContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_eclfield_recref);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(OPAREN);
			setState(125);
			match(TOKEN);
			setState(126);
			match(CPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Payload_sepContext extends ParserRuleContext {
		public Payload_sepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_payload_sep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterPayload_sep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitPayload_sep(this);
		}
	}

	public final Payload_sepContext payload_sep() throws RecognitionException {
		Payload_sepContext _localctx = new Payload_sepContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_payload_sep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Record_def_inlineContext extends ParserRuleContext {
		public TerminalNode OCURLY() { return getToken(EclRecordParser.OCURLY, 0); }
		public List<Eclfield_declContext> eclfield_decl() {
			return getRuleContexts(Eclfield_declContext.class);
		}
		public Eclfield_declContext eclfield_decl(int i) {
			return getRuleContext(Eclfield_declContext.class,i);
		}
		public TerminalNode CCURLY() { return getToken(EclRecordParser.CCURLY, 0); }
		public TerminalNode SEMI() { return getToken(EclRecordParser.SEMI, 0); }
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public MaxlengthContext maxlength() {
			return getRuleContext(MaxlengthContext.class,0);
		}
		public Record_def_inlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_def_inline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterRecord_def_inline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitRecord_def_inline(this);
		}
	}

	public final Record_def_inlineContext record_def_inline() throws RecognitionException {
		Record_def_inlineContext _localctx = new Record_def_inlineContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_record_def_inline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(OCURLY);
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(131);
				match(COMMA);
				setState(132);
				maxlength();
				}
			}

			setState(135);
			eclfield_decl();
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << OPAREN) | (1L << OCURLY) | (1L << COMMA) | (1L << DATASET_SYM) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) {
				{
				{
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(136);
					match(COMMA);
					}
					}
					setState(141);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(142);
				eclfield_decl();
				}
				}
				setState(147);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(148);
			match(CCURLY);
			setState(149);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Record_defContext extends ParserRuleContext {
		public TerminalNode REC_SYM() { return getToken(EclRecordParser.REC_SYM, 0); }
		public List<Eclfield_declContext> eclfield_decl() {
			return getRuleContexts(Eclfield_declContext.class);
		}
		public Eclfield_declContext eclfield_decl(int i) {
			return getRuleContext(Eclfield_declContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(EclRecordParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(EclRecordParser.SEMI, i);
		}
		public TerminalNode END_SYM() { return getToken(EclRecordParser.END_SYM, 0); }
		public TerminalNode COMMA() { return getToken(EclRecordParser.COMMA, 0); }
		public MaxlengthContext maxlength() {
			return getRuleContext(MaxlengthContext.class,0);
		}
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public Record_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterRecord_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitRecord_def(this);
		}
	}

	public final Record_defContext record_def() throws RecognitionException {
		Record_defContext _localctx = new Record_defContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_record_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(REC_SYM);
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(152);
				match(COMMA);
				setState(153);
				maxlength();
				}
			}

			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12 || _la==T__13) {
				{
				setState(156);
				comment();
				}
			}

			setState(159);
			eclfield_decl();
			setState(160);
			match(SEMI);
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12 || _la==T__13) {
				{
				setState(161);
				comment();
				}
			}

			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << OPAREN) | (1L << OCURLY) | (1L << DATASET_SYM) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) {
				{
				{
				setState(164);
				eclfield_decl();
				setState(165);
				match(SEMI);
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__12 || _la==T__13) {
					{
					setState(166);
					comment();
					}
				}

				}
				}
				setState(173);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(174);
			match(END_SYM);
			setState(175);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Defined_record_defContext extends ParserRuleContext {
		public TerminalNode ASSING_SYM() { return getToken(EclRecordParser.ASSING_SYM, 0); }
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public Record_defContext record_def() {
			return getRuleContext(Record_defContext.class,0);
		}
		public Record_def_inlineContext record_def_inline() {
			return getRuleContext(Record_def_inlineContext.class,0);
		}
		public Defined_record_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defined_record_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterDefined_record_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitDefined_record_def(this);
		}
	}

	public final Defined_record_defContext defined_record_def() throws RecognitionException {
		Defined_record_defContext _localctx = new Defined_record_defContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_defined_record_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(178);
			match(ASSING_SYM);
			setState(181);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REC_SYM:
				{
				setState(179);
				record_def();
				}
				break;
			case OCURLY:
				{
				setState(180);
				record_def_inline();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exploded_dataset_record_defContext extends ParserRuleContext {
		public TerminalNode REC_SYM() { return getToken(EclRecordParser.REC_SYM, 0); }
		public List<Eclfield_declContext> eclfield_decl() {
			return getRuleContexts(Eclfield_declContext.class);
		}
		public Eclfield_declContext eclfield_decl(int i) {
			return getRuleContext(Eclfield_declContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(EclRecordParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(EclRecordParser.SEMI, i);
		}
		public TerminalNode END_SYM() { return getToken(EclRecordParser.END_SYM, 0); }
		public TerminalNode COMMA() { return getToken(EclRecordParser.COMMA, 0); }
		public MaxlengthContext maxlength() {
			return getRuleContext(MaxlengthContext.class,0);
		}
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public Exploded_dataset_record_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exploded_dataset_record_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterExploded_dataset_record_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitExploded_dataset_record_def(this);
		}
	}

	public final Exploded_dataset_record_defContext exploded_dataset_record_def() throws RecognitionException {
		Exploded_dataset_record_defContext _localctx = new Exploded_dataset_record_defContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_exploded_dataset_record_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(REC_SYM);
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(184);
				match(COMMA);
				setState(185);
				maxlength();
				}
			}

			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12 || _la==T__13) {
				{
				setState(188);
				comment();
				}
			}

			setState(191);
			eclfield_decl();
			setState(192);
			match(SEMI);
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12 || _la==T__13) {
				{
				setState(193);
				comment();
				}
			}

			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << OPAREN) | (1L << OCURLY) | (1L << DATASET_SYM) | (1L << TOKEN) | (1L << UTOKEN))) != 0)) {
				{
				{
				setState(196);
				eclfield_decl();
				setState(197);
				match(SEMI);
				setState(199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__12 || _la==T__13) {
					{
					setState(198);
					comment();
					}
				}

				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(206);
			match(END_SYM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Inline_dataset_record_defContext extends ParserRuleContext {
		public TerminalNode OCURLY() { return getToken(EclRecordParser.OCURLY, 0); }
		public List<Eclfield_declContext> eclfield_decl() {
			return getRuleContexts(Eclfield_declContext.class);
		}
		public Eclfield_declContext eclfield_decl(int i) {
			return getRuleContext(Eclfield_declContext.class,i);
		}
		public TerminalNode CCURLY() { return getToken(EclRecordParser.CCURLY, 0); }
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public Inline_dataset_record_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inline_dataset_record_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterInline_dataset_record_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitInline_dataset_record_def(this);
		}
	}

	public final Inline_dataset_record_defContext inline_dataset_record_def() throws RecognitionException {
		Inline_dataset_record_defContext _localctx = new Inline_dataset_record_defContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_inline_dataset_record_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(OCURLY);
			setState(209);
			eclfield_decl();
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(210);
				match(COMMA);
				setState(211);
				eclfield_decl();
				}
				}
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217);
			match(CCURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Record_defsContext extends ParserRuleContext {
		public Record_def_inlineContext record_def_inline() {
			return getRuleContext(Record_def_inlineContext.class,0);
		}
		public Record_defContext record_def() {
			return getRuleContext(Record_defContext.class,0);
		}
		public Defined_record_defContext defined_record_def() {
			return getRuleContext(Defined_record_defContext.class,0);
		}
		public Record_defsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_defs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterRecord_defs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitRecord_defs(this);
		}
	}

	public final Record_defsContext record_defs() throws RecognitionException {
		Record_defsContext _localctx = new Record_defsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_record_defs);
		try {
			setState(222);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OCURLY:
				enterOuterAlt(_localctx, 1);
				{
				setState(219);
				record_def_inline();
				}
				break;
			case REC_SYM:
				enterOuterAlt(_localctx, 2);
				{
				setState(220);
				record_def();
				}
				break;
			case TOKEN:
			case UTOKEN:
				enterOuterAlt(_localctx, 3);
				{
				setState(221);
				defined_record_def();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Nested_dataset_declContext extends ParserRuleContext {
		public TerminalNode DATASET_SYM() { return getToken(EclRecordParser.DATASET_SYM, 0); }
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public List<TerminalNode> UTOKEN() { return getTokens(EclRecordParser.UTOKEN); }
		public TerminalNode UTOKEN(int i) {
			return getToken(EclRecordParser.UTOKEN, i);
		}
		public List<TerminalNode> TOKEN() { return getTokens(EclRecordParser.TOKEN); }
		public TerminalNode TOKEN(int i) {
			return getToken(EclRecordParser.TOKEN, i);
		}
		public TerminalNode OCURLY() { return getToken(EclRecordParser.OCURLY, 0); }
		public OptsContext opts() {
			return getRuleContext(OptsContext.class,0);
		}
		public TerminalNode CCURLY() { return getToken(EclRecordParser.CCURLY, 0); }
		public Nested_dataset_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nested_dataset_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterNested_dataset_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitNested_dataset_decl(this);
		}
	}

	public final Nested_dataset_declContext nested_dataset_decl() throws RecognitionException {
		Nested_dataset_declContext _localctx = new Nested_dataset_declContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_nested_dataset_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(DATASET_SYM);
			setState(225);
			match(OPAREN);
			setState(226);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(227);
			match(CPAREN);
			setState(228);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(233);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(229);
				match(OCURLY);
				setState(230);
				opts();
				setState(231);
				match(CCURLY);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Nested_inline_dataset_declContext extends ParserRuleContext {
		public TerminalNode DATASET_SYM() { return getToken(EclRecordParser.DATASET_SYM, 0); }
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public Exploded_dataset_record_defContext exploded_dataset_record_def() {
			return getRuleContext(Exploded_dataset_record_defContext.class,0);
		}
		public Inline_dataset_record_defContext inline_dataset_record_def() {
			return getRuleContext(Inline_dataset_record_defContext.class,0);
		}
		public TerminalNode OCURLY() { return getToken(EclRecordParser.OCURLY, 0); }
		public OptsContext opts() {
			return getRuleContext(OptsContext.class,0);
		}
		public TerminalNode CCURLY() { return getToken(EclRecordParser.CCURLY, 0); }
		public Nested_inline_dataset_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nested_inline_dataset_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterNested_inline_dataset_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitNested_inline_dataset_decl(this);
		}
	}

	public final Nested_inline_dataset_declContext nested_inline_dataset_decl() throws RecognitionException {
		Nested_inline_dataset_declContext _localctx = new Nested_inline_dataset_declContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_nested_inline_dataset_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(DATASET_SYM);
			setState(236);
			match(OPAREN);
			setState(239);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REC_SYM:
				{
				setState(237);
				exploded_dataset_record_def();
				}
				break;
			case OCURLY:
				{
				setState(238);
				inline_dataset_record_def();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(241);
			match(CPAREN);
			setState(242);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(247);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(243);
				match(OCURLY);
				setState(244);
				opts();
				setState(245);
				match(CCURLY);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptsContext extends ParserRuleContext {
		public List<OptContext> opt() {
			return getRuleContexts(OptContext.class);
		}
		public OptContext opt(int i) {
			return getRuleContext(OptContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public OptsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_opts; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterOpts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitOpts(this);
		}
	}

	public final OptsContext opts() throws RecognitionException {
		OptsContext _localctx = new OptsContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_opts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			opt();
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(250);
				match(COMMA);
				setState(251);
				opt();
				}
				}
				setState(256);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptContext extends ParserRuleContext {
		public MaxlengthContext maxlength() {
			return getRuleContext(MaxlengthContext.class,0);
		}
		public MaxcountContext maxcount() {
			return getRuleContext(MaxcountContext.class,0);
		}
		public DefaultvalContext defaultval() {
			return getRuleContext(DefaultvalContext.class,0);
		}
		public XpathContext xpath() {
			return getRuleContext(XpathContext.class,0);
		}
		public XmldefaultvalContext xmldefaultval() {
			return getRuleContext(XmldefaultvalContext.class,0);
		}
		public BlobContext blob() {
			return getRuleContext(BlobContext.class,0);
		}
		public OptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_opt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterOpt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitOpt(this);
		}
	}

	public final OptContext opt() throws RecognitionException {
		OptContext _localctx = new OptContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_opt);
		try {
			setState(263);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
			case T__3:
			case T__5:
				enterOuterAlt(_localctx, 1);
				{
				setState(257);
				maxlength();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(258);
				maxcount();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(259);
				defaultval();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(260);
				xpath();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(261);
				xmldefaultval();
				}
				break;
			case T__6:
			case T__7:
				enterOuterAlt(_localctx, 6);
				{
				setState(262);
				blob();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MaxlengthContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode INT() { return getToken(EclRecordParser.INT, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public MaxlengthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxlength; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterMaxlength(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitMaxlength(this);
		}
	}

	public final MaxlengthContext maxlength() throws RecognitionException {
		MaxlengthContext _localctx = new MaxlengthContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_maxlength);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				{
				setState(265);
				match(T__2);
				setState(266);
				match(OPAREN);
				setState(267);
				match(INT);
				setState(268);
				match(CPAREN);
				}
				break;
			case T__3:
				{
				setState(269);
				match(T__3);
				setState(270);
				match(OPAREN);
				setState(271);
				match(INT);
				setState(272);
				match(CPAREN);
				}
				break;
			case T__5:
				{
				setState(273);
				match(T__5);
				setState(274);
				match(OPAREN);
				setState(275);
				match(INT);
				setState(276);
				match(CPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlobContext extends ParserRuleContext {
		public BlobContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blob; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterBlob(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitBlob(this);
		}
	}

	public final BlobContext blob() throws RecognitionException {
		BlobContext _localctx = new BlobContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_blob);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(279);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==T__7) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MaxcountContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode INT() { return getToken(EclRecordParser.INT, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public MaxcountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxcount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterMaxcount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitMaxcount(this);
		}
	}

	public final MaxcountContext maxcount() throws RecognitionException {
		MaxcountContext _localctx = new MaxcountContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_maxcount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			match(T__8);
			setState(282);
			match(OPAREN);
			setState(283);
			match(INT);
			setState(284);
			match(CPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefaultvalContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode STRING() { return getToken(EclRecordParser.STRING, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public DefaultvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterDefaultval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitDefaultval(this);
		}
	}

	public final DefaultvalContext defaultval() throws RecognitionException {
		DefaultvalContext _localctx = new DefaultvalContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_defaultval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			match(T__9);
			setState(287);
			match(OPAREN);
			setState(288);
			match(STRING);
			setState(289);
			match(CPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class XpathContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode STRING() { return getToken(EclRecordParser.STRING, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public XpathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xpath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterXpath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitXpath(this);
		}
	}

	public final XpathContext xpath() throws RecognitionException {
		XpathContext _localctx = new XpathContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_xpath);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(T__10);
			setState(292);
			match(OPAREN);
			setState(293);
			match(STRING);
			setState(294);
			match(CPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class XmldefaultvalContext extends ParserRuleContext {
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public TerminalNode STRING() { return getToken(EclRecordParser.STRING, 0); }
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public XmldefaultvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xmldefaultval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterXmldefaultval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitXmldefaultval(this);
		}
	}

	public final XmldefaultvalContext xmldefaultval() throws RecognitionException {
		XmldefaultvalContext _localctx = new XmldefaultvalContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_xmldefaultval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(T__11);
			setState(297);
			match(OPAREN);
			setState(298);
			match(STRING);
			setState(299);
			match(CPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotation_nameContext extends ParserRuleContext {
		public TerminalNode ATOKEN() { return getToken(EclRecordParser.ATOKEN, 0); }
		public Annotation_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAnnotation_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAnnotation_name(this);
		}
	}

	public final Annotation_nameContext annotation_name() throws RecognitionException {
		Annotation_nameContext _localctx = new Annotation_nameContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_annotation_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			match(ATOKEN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotation_paramContext extends ParserRuleContext {
		public TerminalNode TOKEN() { return getToken(EclRecordParser.TOKEN, 0); }
		public TerminalNode UTOKEN() { return getToken(EclRecordParser.UTOKEN, 0); }
		public Annotation_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAnnotation_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAnnotation_param(this);
		}
	}

	public final Annotation_paramContext annotation_param() throws RecognitionException {
		Annotation_paramContext _localctx = new Annotation_paramContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_annotation_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			_la = _input.LA(1);
			if ( !(_la==TOKEN || _la==UTOKEN) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotation_argumentsContext extends ParserRuleContext {
		public List<Annotation_paramContext> annotation_param() {
			return getRuleContexts(Annotation_paramContext.class);
		}
		public Annotation_paramContext annotation_param(int i) {
			return getRuleContext(Annotation_paramContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public Annotation_argumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAnnotation_arguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAnnotation_arguments(this);
		}
	}

	public final Annotation_argumentsContext annotation_arguments() throws RecognitionException {
		Annotation_argumentsContext _localctx = new Annotation_argumentsContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_annotation_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			annotation_param();
			setState(310);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(306);
				match(COMMA);
				setState(307);
				annotation_param();
				}
				}
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnnotationContext extends ParserRuleContext {
		public Annotation_nameContext annotation_name() {
			return getRuleContext(Annotation_nameContext.class,0);
		}
		public TerminalNode OPAREN() { return getToken(EclRecordParser.OPAREN, 0); }
		public Annotation_argumentsContext annotation_arguments() {
			return getRuleContext(Annotation_argumentsContext.class,0);
		}
		public TerminalNode CPAREN() { return getToken(EclRecordParser.CPAREN, 0); }
		public AnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitAnnotation(this);
		}
	}

	public final AnnotationContext annotation() throws RecognitionException {
		AnnotationContext _localctx = new AnnotationContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_annotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(313);
			annotation_name();
			setState(318);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(314);
				match(OPAREN);
				setState(315);
				annotation_arguments();
				setState(316);
				match(CPAREN);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommentContext extends ParserRuleContext {
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(EclRecordParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(EclRecordParser.COMMA, i);
		}
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EclRecordListener ) ((EclRecordListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_comment);
		try {
			int _alt;
			setState(364);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__12:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(320);
				match(T__12);
				setState(322);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
				case 1:
					{
					setState(321);
					annotation();
					}
					break;
				}
				setState(328);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(324);
						match(COMMA);
						setState(325);
						annotation();
						}
						} 
					}
					setState(330);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				}
				setState(334);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(331);
						matchWildcard();
						}
						} 
					}
					setState(336);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
				}
				}
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(337);
				match(T__13);
				setState(339);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
				case 1:
					{
					setState(338);
					annotation();
					}
					break;
				}
				setState(345);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(341);
						match(COMMA);
						setState(342);
						annotation();
						}
						} 
					}
					setState(347);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				setState(351);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(348);
						matchWildcard();
						}
						} 
					}
					setState(353);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
				}
				setState(362);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
				case 1:
					{
					setState(357);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
					while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1+1 ) {
							{
							{
							setState(354);
							matchWildcard();
							}
							} 
						}
						setState(359);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
					}
					setState(360);
					match(T__14);
					}
					break;
				case 2:
					{
					setState(361);
					match(T__14);
					}
					break;
				}
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u0171\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\3\2\7\2E\n\2\f\2\16\2H\13\2\3\3\3\3\3\4\3\4\3\4\7\4O\n\4\f\4\16"+
		"\4R\13\4\3\5\3\5\3\5\7\5W\n\5\f\5\16\5Z\13\5\3\6\3\6\3\6\3\6\3\7\3\7\3"+
		"\7\7\7c\n\7\f\7\16\7f\13\7\3\b\3\b\3\b\3\b\3\b\3\b\5\bn\n\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\5\bv\n\b\3\t\5\ty\n\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\r\3\r\3\r\5\r\u0088\n\r\3\r\3\r\7\r\u008c\n\r\f\r\16\r\u008f"+
		"\13\r\3\r\7\r\u0092\n\r\f\r\16\r\u0095\13\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\5\16\u009d\n\16\3\16\5\16\u00a0\n\16\3\16\3\16\3\16\5\16\u00a5\n\16\3"+
		"\16\3\16\3\16\5\16\u00aa\n\16\7\16\u00ac\n\16\f\16\16\16\u00af\13\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u00b8\n\17\3\20\3\20\3\20\5\20"+
		"\u00bd\n\20\3\20\5\20\u00c0\n\20\3\20\3\20\3\20\5\20\u00c5\n\20\3\20\3"+
		"\20\3\20\5\20\u00ca\n\20\7\20\u00cc\n\20\f\20\16\20\u00cf\13\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\7\21\u00d7\n\21\f\21\16\21\u00da\13\21\3\21\3"+
		"\21\3\22\3\22\3\22\5\22\u00e1\n\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\5\23\u00ec\n\23\3\24\3\24\3\24\3\24\5\24\u00f2\n\24\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\5\24\u00fa\n\24\3\25\3\25\3\25\7\25\u00ff\n\25"+
		"\f\25\16\25\u0102\13\25\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u010a\n\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0118"+
		"\n\27\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33"+
		"\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3\37\7\37\u0137\n\37\f\37\16\37\u013a\13\37\3 \3 \3 \3 \3 \5 \u0141"+
		"\n \3!\3!\5!\u0145\n!\3!\3!\7!\u0149\n!\f!\16!\u014c\13!\3!\7!\u014f\n"+
		"!\f!\16!\u0152\13!\3!\3!\5!\u0156\n!\3!\3!\7!\u015a\n!\f!\16!\u015d\13"+
		"!\3!\7!\u0160\n!\f!\16!\u0163\13!\3!\7!\u0166\n!\f!\16!\u0169\13!\3!\3"+
		"!\5!\u016d\n!\5!\u016f\n!\3!\5\u0150\u0161\u0167\2\"\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@\2\7\4\2\36\37!\"\3\2\3"+
		"\4\3\2!\"\4\2\5\6!\"\3\2\t\n\2\u0181\2B\3\2\2\2\4I\3\2\2\2\6K\3\2\2\2"+
		"\bS\3\2\2\2\n[\3\2\2\2\f_\3\2\2\2\16u\3\2\2\2\20x\3\2\2\2\22|\3\2\2\2"+
		"\24~\3\2\2\2\26\u0082\3\2\2\2\30\u0084\3\2\2\2\32\u0099\3\2\2\2\34\u00b3"+
		"\3\2\2\2\36\u00b9\3\2\2\2 \u00d2\3\2\2\2\"\u00e0\3\2\2\2$\u00e2\3\2\2"+
		"\2&\u00ed\3\2\2\2(\u00fb\3\2\2\2*\u0109\3\2\2\2,\u0117\3\2\2\2.\u0119"+
		"\3\2\2\2\60\u011b\3\2\2\2\62\u0120\3\2\2\2\64\u0125\3\2\2\2\66\u012a\3"+
		"\2\2\28\u012f\3\2\2\2:\u0131\3\2\2\2<\u0133\3\2\2\2>\u013b\3\2\2\2@\u016e"+
		"\3\2\2\2BF\5\"\22\2CE\5\"\22\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2"+
		"G\3\3\2\2\2HF\3\2\2\2IJ\t\2\2\2J\5\3\2\2\2KP\5\4\3\2LM\7\26\2\2MO\5\4"+
		"\3\2NL\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\7\3\2\2\2RP\3\2\2\2SX\7"+
		"!\2\2TU\7\26\2\2UW\7!\2\2VT\3\2\2\2WZ\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y\t\3"+
		"\2\2\2ZX\3\2\2\2[\\\5\4\3\2\\]\7\30\2\2]^\5\6\4\2^\13\3\2\2\2_d\5\n\6"+
		"\2`a\7\26\2\2ac\5\n\6\2b`\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2e\r\3\2"+
		"\2\2fd\3\2\2\2gh\5\20\t\2hm\5\22\n\2ij\7\24\2\2jk\5(\25\2kl\7\25\2\2l"+
		"n\3\2\2\2mi\3\2\2\2mn\3\2\2\2nv\3\2\2\2ov\5$\23\2pv\5&\24\2qr\5 \21\2"+
		"rs\5\22\n\2sv\3\2\2\2tv\5\24\13\2ug\3\2\2\2uo\3\2\2\2up\3\2\2\2uq\3\2"+
		"\2\2ut\3\2\2\2v\17\3\2\2\2wy\t\3\2\2xw\3\2\2\2xy\3\2\2\2yz\3\2\2\2z{\t"+
		"\4\2\2{\21\3\2\2\2|}\t\5\2\2}\23\3\2\2\2~\177\7\22\2\2\177\u0080\7!\2"+
		"\2\u0080\u0081\7\23\2\2\u0081\25\3\2\2\2\u0082\u0083\7\7\2\2\u0083\27"+
		"\3\2\2\2\u0084\u0087\7\24\2\2\u0085\u0086\7\26\2\2\u0086\u0088\5,\27\2"+
		"\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0093"+
		"\5\16\b\2\u008a\u008c\7\26\2\2\u008b\u008a\3\2\2\2\u008c\u008f\3\2\2\2"+
		"\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0090\3\2\2\2\u008f\u008d"+
		"\3\2\2\2\u0090\u0092\5\16\b\2\u0091\u008d\3\2\2\2\u0092\u0095\3\2\2\2"+
		"\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0096\3\2\2\2\u0095\u0093"+
		"\3\2\2\2\u0096\u0097\7\25\2\2\u0097\u0098\7\27\2\2\u0098\31\3\2\2\2\u0099"+
		"\u009c\7\32\2\2\u009a\u009b\7\26\2\2\u009b\u009d\5,\27\2\u009c\u009a\3"+
		"\2\2\2\u009c\u009d\3\2\2\2\u009d\u009f\3\2\2\2\u009e\u00a0\5@!\2\u009f"+
		"\u009e\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\5\16"+
		"\b\2\u00a2\u00a4\7\27\2\2\u00a3\u00a5\5@!\2\u00a4\u00a3\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00ad\3\2\2\2\u00a6\u00a7\5\16\b\2\u00a7\u00a9\7"+
		"\27\2\2\u00a8\u00aa\5@!\2\u00a9\u00a8\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa"+
		"\u00ac\3\2\2\2\u00ab\u00a6\3\2\2\2\u00ac\u00af\3\2\2\2\u00ad\u00ab\3\2"+
		"\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00b0\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0"+
		"\u00b1\7\33\2\2\u00b1\u00b2\7\27\2\2\u00b2\33\3\2\2\2\u00b3\u00b4\t\4"+
		"\2\2\u00b4\u00b7\7\31\2\2\u00b5\u00b8\5\32\16\2\u00b6\u00b8\5\30\r\2\u00b7"+
		"\u00b5\3\2\2\2\u00b7\u00b6\3\2\2\2\u00b8\35\3\2\2\2\u00b9\u00bc\7\32\2"+
		"\2\u00ba\u00bb\7\26\2\2\u00bb\u00bd\5,\27\2\u00bc\u00ba\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd\u00bf\3\2\2\2\u00be\u00c0\5@!\2\u00bf\u00be\3\2\2"+
		"\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\5\16\b\2\u00c2"+
		"\u00c4\7\27\2\2\u00c3\u00c5\5@!\2\u00c4\u00c3\3\2\2\2\u00c4\u00c5\3\2"+
		"\2\2\u00c5\u00cd\3\2\2\2\u00c6\u00c7\5\16\b\2\u00c7\u00c9\7\27\2\2\u00c8"+
		"\u00ca\5@!\2\u00c9\u00c8\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2"+
		"\2\u00cb\u00c6\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce"+
		"\3\2\2\2\u00ce\u00d0\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00d1\7\33\2\2"+
		"\u00d1\37\3\2\2\2\u00d2\u00d3\7\24\2\2\u00d3\u00d8\5\16\b\2\u00d4\u00d5"+
		"\7\26\2\2\u00d5\u00d7\5\16\b\2\u00d6\u00d4\3\2\2\2\u00d7\u00da\3\2\2\2"+
		"\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00db\u00dc\7\25\2\2\u00dc!\3\2\2\2\u00dd\u00e1\5\30\r\2\u00de"+
		"\u00e1\5\32\16\2\u00df\u00e1\5\34\17\2\u00e0\u00dd\3\2\2\2\u00e0\u00de"+
		"\3\2\2\2\u00e0\u00df\3\2\2\2\u00e1#\3\2\2\2\u00e2\u00e3\7\34\2\2\u00e3"+
		"\u00e4\7\22\2\2\u00e4\u00e5\t\4\2\2\u00e5\u00e6\7\23\2\2\u00e6\u00eb\t"+
		"\4\2\2\u00e7\u00e8\7\24\2\2\u00e8\u00e9\5(\25\2\u00e9\u00ea\7\25\2\2\u00ea"+
		"\u00ec\3\2\2\2\u00eb\u00e7\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec%\3\2\2\2"+
		"\u00ed\u00ee\7\34\2\2\u00ee\u00f1\7\22\2\2\u00ef\u00f2\5\36\20\2\u00f0"+
		"\u00f2\5 \21\2\u00f1\u00ef\3\2\2\2\u00f1\u00f0\3\2\2\2\u00f2\u00f3\3\2"+
		"\2\2\u00f3\u00f4\7\23\2\2\u00f4\u00f9\t\4\2\2\u00f5\u00f6\7\24\2\2\u00f6"+
		"\u00f7\5(\25\2\u00f7\u00f8\7\25\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00f5\3"+
		"\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\'\3\2\2\2\u00fb\u0100\5*\26\2\u00fc\u00fd"+
		"\7\26\2\2\u00fd\u00ff\5*\26\2\u00fe\u00fc\3\2\2\2\u00ff\u0102\3\2\2\2"+
		"\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101)\3\2\2\2\u0102\u0100\3"+
		"\2\2\2\u0103\u010a\5,\27\2\u0104\u010a\5\60\31\2\u0105\u010a\5\62\32\2"+
		"\u0106\u010a\5\64\33\2\u0107\u010a\5\66\34\2\u0108\u010a\5.\30\2\u0109"+
		"\u0103\3\2\2\2\u0109\u0104\3\2\2\2\u0109\u0105\3\2\2\2\u0109\u0106\3\2"+
		"\2\2\u0109\u0107\3\2\2\2\u0109\u0108\3\2\2\2\u010a+\3\2\2\2\u010b\u010c"+
		"\7\5\2\2\u010c\u010d\7\22\2\2\u010d\u010e\7\36\2\2\u010e\u0118\7\23\2"+
		"\2\u010f\u0110\7\6\2\2\u0110\u0111\7\22\2\2\u0111\u0112\7\36\2\2\u0112"+
		"\u0118\7\23\2\2\u0113\u0114\7\b\2\2\u0114\u0115\7\22\2\2\u0115\u0116\7"+
		"\36\2\2\u0116\u0118\7\23\2\2\u0117\u010b\3\2\2\2\u0117\u010f\3\2\2\2\u0117"+
		"\u0113\3\2\2\2\u0118-\3\2\2\2\u0119\u011a\t\6\2\2\u011a/\3\2\2\2\u011b"+
		"\u011c\7\13\2\2\u011c\u011d\7\22\2\2\u011d\u011e\7\36\2\2\u011e\u011f"+
		"\7\23\2\2\u011f\61\3\2\2\2\u0120\u0121\7\f\2\2\u0121\u0122\7\22\2\2\u0122"+
		"\u0123\7\37\2\2\u0123\u0124\7\23\2\2\u0124\63\3\2\2\2\u0125\u0126\7\r"+
		"\2\2\u0126\u0127\7\22\2\2\u0127\u0128\7\37\2\2\u0128\u0129\7\23\2\2\u0129"+
		"\65\3\2\2\2\u012a\u012b\7\16\2\2\u012b\u012c\7\22\2\2\u012c\u012d\7\37"+
		"\2\2\u012d\u012e\7\23\2\2\u012e\67\3\2\2\2\u012f\u0130\7 \2\2\u01309\3"+
		"\2\2\2\u0131\u0132\t\4\2\2\u0132;\3\2\2\2\u0133\u0138\5:\36\2\u0134\u0135"+
		"\7\26\2\2\u0135\u0137\5:\36\2\u0136\u0134\3\2\2\2\u0137\u013a\3\2\2\2"+
		"\u0138\u0136\3\2\2\2\u0138\u0139\3\2\2\2\u0139=\3\2\2\2\u013a\u0138\3"+
		"\2\2\2\u013b\u0140\58\35\2\u013c\u013d\7\22\2\2\u013d\u013e\5<\37\2\u013e"+
		"\u013f\7\23\2\2\u013f\u0141\3\2\2\2\u0140\u013c\3\2\2\2\u0140\u0141\3"+
		"\2\2\2\u0141?\3\2\2\2\u0142\u0144\7\17\2\2\u0143\u0145\5> \2\u0144\u0143"+
		"\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u014a\3\2\2\2\u0146\u0147\7\26\2\2"+
		"\u0147\u0149\5> \2\u0148\u0146\3\2\2\2\u0149\u014c\3\2\2\2\u014a\u0148"+
		"\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0150\3\2\2\2\u014c\u014a\3\2\2\2\u014d"+
		"\u014f\13\2\2\2\u014e\u014d\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u0151\3"+
		"\2\2\2\u0150\u014e\3\2\2\2\u0151\u016f\3\2\2\2\u0152\u0150\3\2\2\2\u0153"+
		"\u0155\7\20\2\2\u0154\u0156\5> \2\u0155\u0154\3\2\2\2\u0155\u0156\3\2"+
		"\2\2\u0156\u015b\3\2\2\2\u0157\u0158\7\26\2\2\u0158\u015a\5> \2\u0159"+
		"\u0157\3\2\2\2\u015a\u015d\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2"+
		"\2\2\u015c\u0161\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0160\13\2\2\2\u015f"+
		"\u015e\3\2\2\2\u0160\u0163\3\2\2\2\u0161\u0162\3\2\2\2\u0161\u015f\3\2"+
		"\2\2\u0162\u016c\3\2\2\2\u0163\u0161\3\2\2\2\u0164\u0166\13\2\2\2\u0165"+
		"\u0164\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0168\3\2\2\2\u0167\u0165\3\2"+
		"\2\2\u0168\u016a\3\2\2\2\u0169\u0167\3\2\2\2\u016a\u016d\7\21\2\2\u016b"+
		"\u016d\7\21\2\2\u016c\u0167\3\2\2\2\u016c\u016b\3\2\2\2\u016d\u016f\3"+
		"\2\2\2\u016e\u0142\3\2\2\2\u016e\u0153\3\2\2\2\u016fA\3\2\2\2*FPXdmux"+
		"\u0087\u008d\u0093\u009c\u009f\u00a4\u00a9\u00ad\u00b7\u00bc\u00bf\u00c4"+
		"\u00c9\u00cd\u00d8\u00e0\u00eb\u00f1\u00f9\u0100\u0109\u0117\u0138\u0140"+
		"\u0144\u014a\u0150\u0155\u015b\u0161\u0167\u016c\u016e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}