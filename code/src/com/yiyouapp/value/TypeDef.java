package com.yiyouapp.value;

import java.util.HashMap;

public class TypeDef {

	public static final int NT_Attention = 1;
	
	public static final int NT_PriseWork = 11;
	public static final int NT_CollectWork = 12;
	public static final int NT_ShareWork = 13;

	public static final int NT_Message = 60;
	public static final int NT_CommentWork = 61;
	public static final int NT_ReplyComment = 62;
	public static final int NT_PublishWork = 70;

	public static final int NT_SendMsgToAll = 89;
	
	// 用户类型
	public static final String[] userType_names = {"大学生", "高中生", "初中生", "画室", "职业画家", "爱好者", "大学老师", "高中老师", "初中老师"};
	public static final String[] userType_values = {"010101", "010102", "010103", "010200", "010300", "010400", "010501", "010502", "010503"};

	// 作品类型
	public static final String[] workType_names = {"素描", "色彩", "速写", "油画", "漫画", "国画", "书法", "其它"};
	public static final String[] workType_values = {"010100", "010200", "010300", "010400", "010500", "010600", "010700", "010800"};

	public static final String[][] workSubtype_names = {
		{"人物", "静物", "石膏", "其它"},
		{"人物", "静物", "风景", "其它"},
		{"人物", "场景", "风景", "其它"},
		{"人物", "静物", "风景", "其它"},
		{"原创", "国产", "欧美", "日本", "其它"},
		{"人物", "山水", "花鸟", "其它"}
	};
	
	// 消息描述定义
	public static HashMap<Integer, String> news_desc;
	
	public static void Initialize()
	{
		news_desc = new HashMap<Integer, String>();
		
		/* 通知定义 */
		
		// 针对用户的ID
		news_desc.put(NT_Attention, "关注了你");
		
		// 针对作品的ID
		news_desc.put(NT_PriseWork, "赞了你的作品");
		news_desc.put(NT_CollectWork, "收藏了你的作品");
		news_desc.put(NT_ShareWork, "分享了你的作品");
		
		/* 消息定义 */

		news_desc.put(NT_Message, "给你留了言");
		news_desc.put(NT_CommentWork, "评论了你的作品");
		news_desc.put(NT_ReplyComment, "回复了你的评论");
		news_desc.put(NT_PublishWork, "发布了新作品");

		// 发布了针对所有用户的消息
		news_desc.put(NT_SendMsgToAll, "发布了消息");
	}
	
	public static String getNewsDesc(int newsType)
	{
		if(news_desc.containsKey(newsType))
			return news_desc.get(newsType);
		else
			return "";
	}
}
