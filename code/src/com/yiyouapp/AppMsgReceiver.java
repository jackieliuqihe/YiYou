package com.yiyouapp;

import java.util.List;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.yiyouapp.cmd.AccountCmd;
import com.yiyouapp.utils.NotificationUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.value.ChatMsg;
import com.yiyouapp.value.TypeDef;
import com.yiyouapp.value.UserNews;

/**
 * Push��Ϣ����receiver�����д����Ҫ�Ļص������� һ����˵�� onBind�Ǳ���ģ���������startWork����ֵ��
 * onMessage��������͸����Ϣ�� onSetTags��onDelTags��onListTags��tag��ز����Ļص���
 * onNotificationClicked��֪ͨ�����ʱ�ص��� onUnbind��stopWork�ӿڵķ���ֵ�ص�
 * 
 * ����ֵ�е�errorCode���������£� 
 *  0 - Success
 *  10001 - Network Problem
 *  30600 - Internal Server Error
 *  30601 - Method Not Allowed 
 *  30602 - Request Params Not Valid
 *  30603 - Authentication Failed 
 *  30604 - Quota Use Up Payment Required 
 *  30605 - Data Required Not Found 
 *  30606 - Request Time Expires Timeout 
 *  30607 - Channel Token Timeout 
 *  30608 - Bind Relation Not Found 
 *  30609 - Bind Number Too Many
 * 
 * �����������Ϸ��ش���ʱ��������Ͳ����������⣬����ͬһ����ķ���ֵrequestId��errorCode��ϵ����׷�����⡣
 * 
 */
public class AppMsgReceiver extends FrontiaPushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = FrontiaPushMessageReceiver.class
            .getSimpleName();

    /**
     * ����PushManager.startWork��sdk����push
     * server�������������������첽�ġ�������Ľ��ͨ��onBind���ء� �������Ҫ�õ������ͣ���Ҫ�������ȡ��channel
     * id��user id�ϴ���Ӧ��server�У��ٵ���server�ӿ���channel id��user id�������ֻ������û����͡�
     * 
     * @param context
     *            BroadcastReceiver��ִ��Context
     * @param errorCode
     *            �󶨽ӿڷ���ֵ��0 - �ɹ�
     * @param appid
     *            Ӧ��id��errorCode��0ʱΪnull
     * @param userId
     *            Ӧ��user id��errorCode��0ʱΪnull
     * @param channelId
     *            Ӧ��channel id��errorCode��0ʱΪnull
     * @param requestId
     *            �����˷��������id����׷������ʱ���ã�
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // �󶨳ɹ��������Ѱ�flag��������Ч�ļ��ٲ���Ҫ�İ�����
        if (errorCode == 0) {
        	AppConfig.settings.setDeviceUserID(userId);
        	AppConfig.settings.setDeviceChannelID(channelId);

        	AppConfig.settings.setDeviceBind(true);
        	
        	// ֪ͨ�ύ�豸ID
			AppFacade.getInstance().sendNotification(AccountCmd.SET_DEVICE_INFO);
			
        }
        
    }

    /**
     * ����͸����Ϣ�ĺ�����
     * 
     * @param context
     *            ������
     * @param message
     *            ���͵���Ϣ
     * @param customContentString
     *            �Զ�������,Ϊ�ջ���json�ַ���
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "͸����Ϣ message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ͸����Ϣ����ʱ�Զ������������õļ���ֵ
        if (!TextUtils.isEmpty(message)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(message);
                
                int action = customJson.getInt("action");
                JSONObject data = customJson.getJSONObject("data");

                // 60 - 89 ֮����Ϊ����Ϣ
                if(action < 60 || action > 89)
                	return;
                
                UserNews news;

            	String title;
            	String msg;
            	
            	Intent intent;
            	
                switch(action)
                {
                case TypeDef.NT_Message: // ���û��Ի�
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.avatar64_path = StringUtil.getString(data.getString("avatar_path")).replace("*", "64");
	                news.news = data.getString("msg");
	                
	                NotificationUtil.msgNavigate(context, news);
	                break;
                case TypeDef.NT_CommentWork: // �û�����Ʒ������
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.extra_data = data.getInt("work_id");
	                news.news = data.getString("comment");
	                
	                NotificationUtil.commentNavigate(context, news);
	                break;
                case TypeDef.NT_ReplyComment: // �ظ�����
	                news = new UserNews();
	
	                news.news_type = action;
	                news.user_id = data.getInt("user_id");
	                news.user_name = data.getString("user_name");
	                news.extra_data = data.getInt("work_id");
	                news.news = data.getString("comment");
	                
	                NotificationUtil.commentNavigate(context, news);
	                break;
                case TypeDef.NT_PublishWork: // �û�����Ʒ��֪ͨ
	                
                	int user_id = data.getInt("user_id");
	                String user_name = data.getString("user_name");
	                //data.getInt("work_id");
	                msg = data.getString("msg");

	    			intent = new Intent(context, WorkListActivity.class);
	    			
	    			intent.putExtra(AppConfig.INTENT_USER_ID, user_id);
	    			intent.putExtra(AppConfig.INTENT_USER_NAME, user_name + "����Ʒ");
	    			intent.putExtra(AppConfig.INTENT_COLLECT, false);

	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    			
    		        NotificationUtil.notify(context, intent, msg, user_name, msg);
                	break;
                case TypeDef.NT_SendMsgToAll: // �������˵�ͨ����Ϣ
                	title = data.getString("title");
                	msg = data.getString("msg");
                	
                	intent = new Intent(context, MainActivity.class);
                	
    		        NotificationUtil.notify(context, intent, msg, title, msg);
                	break;
                default:
                	break;
                }
                
            } catch (JSONException e) {
            	
            }
        }

    }

    /**
     * ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
     * 
     * @param context
     *            ������
     * @param title
     *            ���͵�֪ͨ�ı���
     * @param description
     *            ���͵�֪ͨ������
     * @param customContentString
     *            �Զ������ݣ�Ϊ�ջ���json�ַ���
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "֪ͨ��� title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);

                UserNews news = new UserNews();
                
                news.news_type = customJson.getInt("action");
                news.user_id = customJson.getInt("user_id");
                news.user_name = title;
                news.avatar64_path = StringUtil.getString(customJson.getString("avatar_path")).replace("*", "64");
                news.extra_data = customJson.getInt("data");
                
                NotificationUtil.newsNavigate(context, news);
                
                NotificationUtil.setNewsViewed2(context, news);
                
            } catch (JSONException e) {
            }
        }

    }

    /**
     * setTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ����óɹ�����0��ʾ����tag�����þ�ʧ�ܡ�
     * @param successTags
     *            ���óɹ���tag
     * @param failTags
     *            ����ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * delTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ�ɾ���ɹ�����0��ʾ����tag��ɾ��ʧ�ܡ�
     * @param successTags
     *            �ɹ�ɾ����tag
     * @param failTags
     *            ɾ��ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

    }

    /**
     * listTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�о�tag�ɹ�����0��ʾʧ�ܡ�
     * @param tags
     *            ��ǰӦ�����õ�����tag��
     * @param requestId
     *            ������������͵������id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

    }

    /**
     * PushManager.stopWork() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�������ͽ�󶨳ɹ�����0��ʾʧ�ܡ�
     * @param requestId
     *            ������������͵������id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // ��󶨳ɹ�������δ��flag��
        if (errorCode == 0) {
        	AppConfig.settings.setDeviceBind(false);
        }
        
    }

}
