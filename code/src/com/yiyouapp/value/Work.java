package com.yiyouapp.value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;

public class Work implements Serializable {

	public int user_id;
	public String user_name;
	public String user_type;
	public String sex;
	public String avatar_path;

	public double longitude;
	public double latitude;
	public double distance;
	public boolean has_location;
	
	public String city;
	
	public int works_id;

	public String work_type;
	
	// ��Ʒ����
	public String work_desc;
	
	// ��Ʒ�ϴ������ʱ��� 
	public int elapsed_secs;
	// ��Ʒ��ȡ����ʱ���豸ʱ��
	public Date device_time;

	// �޵�����
	public int prise_count;
	// �ղص�����
	public int collect_count;
	// ��������
	public int comment_count;

	// �û��Ƿ���
	public boolean i_prise;
	// �û��Ƿ��ղ�
	public boolean i_collect;
	
	// ��Ʒͼ����
	public int image_count;
	
	// ��Ʒ·��ģʽ
	public String path_pattern;
	
	// ��Ʒ����ͼ·��
	public String[] thumb_path;
	// ��Ʒ��ͼ·��
	public String[] file_path;

	// ͷ�����ʱ��
	public String avatar_uptime;
	
	public Uri thumb_uri;
	public Uri avatar_uri;
	
	// �ݺ��
	public float thumb1_ratio;

	/*
	public boolean relativeEqualWith(Object o) 
	{
		Work work = (Work)o;
		if(work == null)
			return false;
		
		boolean b_int = user_id == work.user_id &&
				works_id == work.works_id;
				
		boolean b_float = Math.abs(thumb_ratio - work.thumb_ratio) < 0.01;
		
		boolean b_str = user_name.equals(work.user_name) &&
				user_type.equals(work.user_type) &&
				sex.equals(work.sex) &&
				avatar_path.equals(work.avatar_path) &&
				city.equals(work.city) &&
				work_type.equals(work.work_type) &&
				work_desc.equals(work.work_desc) &&
				thumb_path.equals(work.thumb_path) &&
				file_path.equals(work.file_path);
		
		return b_int && b_float && b_str;
	}
	*/
}
