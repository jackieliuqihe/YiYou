package com.yiyouapp.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.RequestParams;
import com.yiyouapp.AppConfig;
import com.yiyouapp.AppFacade;
import com.yiyouapp.ChatActivity;
import com.yiyouapp.MainActivity;
import com.yiyouapp.R;
import com.yiyouapp.UserCenterOtherActivity;
import com.yiyouapp.WorkCommentActivity;
import com.yiyouapp.cmd.UserNewsCmd;
import com.yiyouapp.proxy.UserNewsProxy;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserNews;

public class NotificationUtil {

	public static void notify(Context context, Intent intent, String statusText, String title, String content)
	{
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification n = new Notification(R.drawable.ic_launcher, statusText, System.currentTimeMillis());
		
		n.flags = Notification.FLAG_AUTO_CANCEL;
		n.defaults = Notification.DEFAULT_SOUND;
		
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context, R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		n.setLatestEventInfo(context, title, content, contentIntent);
		
		nm.notify(R.string.app_name, n);
	}

	public static void newsNavigate(Context context, UserNews userNews)
	{
		Intent intent;
		
		switch(userNews.news_type)
		{
		case TypeDef.NT_Attention:
			intent = new Intent(context, UserCenterOtherActivity.class);
			
			intent.putExtra(AppConfig.INTENT_USER_ID, userNews.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, userNews.user_name);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);

			break;
		case TypeDef.NT_PriseWork:
			intent = new Intent(context, WorkCommentActivity.class);

			intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);
			break;
		case TypeDef.NT_CollectWork:
			intent = new Intent(context, WorkCommentActivity.class);

			intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);
			break;
		case TypeDef.NT_ShareWork:
			break;
		case TypeDef.NT_Message: // 单用户对话
			intent = new Intent(context, ChatActivity.class);

			intent.putExtra(AppConfig.INTENT_USER_ID, userNews.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, userNews.user_name);
			intent.putExtra(AppConfig.INTENT_AVATAR_PATH, userNews.avatar64_path);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);

			break;
		case TypeDef.NT_CommentWork: // 作品评论
			intent = new Intent(context, WorkCommentActivity.class);

			intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);

			break;
		case TypeDef.NT_ReplyComment: // 回复作品评论
			intent = new Intent(context, WorkCommentActivity.class);

			intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent);

			break;
		}
	}

	// 用户对话的消息处理
	public static void msgNavigate(Context context, UserNews userNews)
	{
		// 判断是否用户开着聊天界面
		ChatActivity act = (ChatActivity)AppFacade.getInstance().retrieveMediator(ChatActivity.NAME);
		
		Intent intent;

		boolean has = (act != null && act.getChatUserID() == userNews.user_id);
		
		if(has)
		{
			if(!act.isTop())
			{
				intent = new Intent(context, ChatActivity.class);
				intent.putExtra(AppConfig.INTENT_USER_ID, userNews.user_id);
				intent.putExtra(AppConfig.INTENT_USER_NAME, userNews.user_name);
				intent.putExtra(AppConfig.INTENT_AVATAR_PATH, userNews.avatar64_path);
				
				String emotionSub = EmotionUtil.getEmotionSubstitude(context, userNews.news);
				
				NotificationUtil.notify(context, intent, emotionSub, userNews.user_name + TypeDef.getNewsDesc(userNews.news_type), emotionSub);
			}
			else
			{
				act.getLatestMsg();
			}
		}
		else
		{
			intent = new Intent(context, ChatActivity.class);
			intent.putExtra(AppConfig.INTENT_USER_ID, userNews.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, userNews.user_name);
			intent.putExtra(AppConfig.INTENT_AVATAR_PATH, userNews.avatar64_path);

			String emotionSub = EmotionUtil.getEmotionSubstitude(context, userNews.news);
			
	        NotificationUtil.notify(context, intent, emotionSub, userNews.user_name + TypeDef.getNewsDesc(userNews.news_type), emotionSub);
		}
	}

	// 用户评论的消息处理
	public static void commentNavigate(Context context, UserNews userNews)
	{
		// 判断是否用户开着聊天界面
		WorkCommentActivity act = (WorkCommentActivity)AppFacade.getInstance().retrieveMediator(WorkCommentActivity.NAME);
		
		Intent intent;

		boolean has = (act != null && act.getWorkId() == userNews.extra_data);
		
		if(has)
		{
			if(!act.isTop())
			{
				intent = new Intent(context, WorkCommentActivity.class);
				intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

				String emotionSub = EmotionUtil.getEmotionSubstitude(context, userNews.news);
				
				NotificationUtil.notify(context, intent, emotionSub, userNews.user_name + TypeDef.getNewsDesc(userNews.news_type), emotionSub);
			}
			else
			{
				act.getLatestMsg();
			}
		}
		else
		{
			intent = new Intent(context, WorkCommentActivity.class);
			intent.putExtra(AppConfig.INTENT_WORK_ID, userNews.extra_data);

			String emotionSub = EmotionUtil.getEmotionSubstitude(context, userNews.news);
			
			NotificationUtil.notify(context, intent, emotionSub, userNews.user_name + TypeDef.getNewsDesc(userNews.news_type), emotionSub);
		}
	}

	public static void setNewsViewed(Context context, UserNews news)
	{
		if(!news.is_view)
		{
			String newsType = String.valueOf(news.news_type);
			if(newsType.length() == 1)
				newsType = "0" + newsType;
			
			RequestParams params = new RequestParams();
			params.add("user_id", String.valueOf(news.user_id));
			params.add("news_type", newsType);
			params.add("extra_data", String.valueOf(news.extra_data));
			
			ReqData req = ReqData.create(context, ReqData.FailHandleType.Notify);
			req.input = params;
			
			AppFacade.getInstance().sendNotification(UserNewsCmd.SET_NEWS_VIEWED, req);
		}
	}
	
	public static void setNewsViewed2(Context context, UserNews news)
	{
		if(!news.is_view)
		{
			String newsType = String.valueOf(news.news_type);
			if(newsType.length() == 1)
				newsType = "0" + newsType;
			
			RequestParams params = new RequestParams();
			params.add("user_id", String.valueOf(news.user_id));
			params.add("news_type", newsType);
			params.add("extra_data", String.valueOf(news.extra_data));
			
			ReqData req = ReqData.create(context, ReqData.FailHandleType.Notify);
			req.input = params;
			
			UserNewsProxy proxy = new UserNewsProxy();
			
			proxy.setNewsViewed(req);
		}
	}
}
