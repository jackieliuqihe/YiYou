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
	
	// �û�����
	public static final String[] userType_names = {"��ѧ��", "������", "������", "����", "ְҵ����", "������", "��ѧ��ʦ", "������ʦ", "������ʦ"};
	public static final String[] userType_values = {"010101", "010102", "010103", "010200", "010300", "010400", "010501", "010502", "010503"};

	// ��Ʒ����
	public static final String[] workType_names = {"����", "ɫ��", "��д", "�ͻ�", "����", "����", "�鷨", "����"};
	public static final String[] workType_values = {"010100", "010200", "010300", "010400", "010500", "010600", "010700", "010800"};

	public static final String[][] workSubtype_names = {
		{"����", "����", "ʯ��", "����"},
		{"����", "����", "�羰", "����"},
		{"����", "����", "�羰", "����"},
		{"����", "����", "�羰", "����"},
		{"ԭ��", "����", "ŷ��", "�ձ�", "����"},
		{"����", "ɽˮ", "����", "����"}
	};
	
	// ��Ϣ��������
	public static HashMap<Integer, String> news_desc;
	
	public static void Initialize()
	{
		news_desc = new HashMap<Integer, String>();
		
		/* ֪ͨ���� */
		
		// ����û���ID
		news_desc.put(NT_Attention, "��ע����");
		
		// �����Ʒ��ID
		news_desc.put(NT_PriseWork, "���������Ʒ");
		news_desc.put(NT_CollectWork, "�ղ��������Ʒ");
		news_desc.put(NT_ShareWork, "�����������Ʒ");
		
		/* ��Ϣ���� */

		news_desc.put(NT_Message, "����������");
		news_desc.put(NT_CommentWork, "�����������Ʒ");
		news_desc.put(NT_ReplyComment, "�ظ����������");
		news_desc.put(NT_PublishWork, "����������Ʒ");

		// ��������������û�����Ϣ
		news_desc.put(NT_SendMsgToAll, "��������Ϣ");
	}
	
	public static String getNewsDesc(int newsType)
	{
		if(news_desc.containsKey(newsType))
			return news_desc.get(newsType);
		else
			return "";
	}
}
