package com.yiyouapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.yiyouapp.adapters.ChatMsgViewAdapter;
import com.yiyouapp.adapters.CommentViewAdapter;
import com.yiyouapp.adapters.ChatMsgViewAdapter.IChatAction;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.UserMsgCmd;
import com.yiyouapp.cmd.WorkCommentCmd;
import com.yiyouapp.cmd.WorkInfoCmd;
import com.yiyouapp.controls.PhotoObtainer;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.UserMsgProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.UserMsgProxy.UserMsgInput;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkCommentProxy.WorkCommentInput;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.proxy.WorkListProxy.WorksListInput;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.DialogUtil;
import com.yiyouapp.utils.EmotionUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SDCardUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.ChatMsg;
import com.yiyouapp.value.CommentMsg;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.SendState;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ChatActivity extends MediatorActivity implements  OnRefreshListener<ListView>, OnClickListener, IChatAction {

	public static final String NAME = "ChatActivity";
	
    private boolean isTop_;
    
	private EditText sendEditText_;
	private PullToRefreshListView msgListView_;
	private ChatMsgViewAdapter adapter_;

    private int user_id_ = 0;
    private String username_;
    private String avatar_path_;

    private Uri tAvatarURI_;
    private Uri mAvatarURI_;
    
    private ArrayList<ChatMsg> msgs_;
    private String contString_;
    
    private View other_input_sv_;
    private ImageView emotion_btn_;
    private InputMethodManager imm_;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		imm_ = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
        // 获取用户信息
		user_id_ = getIntent().getIntExtra(AppConfig.INTENT_USER_ID, 0);
		username_ = getIntent().getStringExtra(AppConfig.INTENT_USER_NAME);
		avatar_path_ = getIntent().getStringExtra(AppConfig.INTENT_AVATAR_PATH);
    	
		if(!StringUtil.isStringEmpty(username_))
		{
			TextView tv = (TextView)findViewById(R.id.top_title_bar_title);
			tv.setText("与" + username_ + "对话");
		}
		
		// 头像
		tAvatarURI_ = AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(avatar_path_));
		File file = new File(AccountUtil.getCurrAccountDir() + AppConfig.FILENAME_AVATAR);
		if(file.isFile() && file.exists())
			mAvatarURI_ = Uri.fromFile(file);
		
		if(tAvatarURI_ == null)
		{
			// 如果对话者没有头像，就加载
			// 需要加载的图片列表
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();

            // 去加载头像
			ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
			InputData input = new InputData();
			
			input.url = StringUtil.getFileUrl(avatar_path_);
			input.cachable = true;
			input.type = ImageType.Avatar;
			input.key = StringUtil.getFileNameFromPath(avatar_path_);
			input.position = -1;
			
			req.input = input;

			reqList.add(req);
			
			sendNotification(GetImageCmd.GET_IMAGE, reqList);
    	}
    
		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		btn = (Button)findViewById(R.id.send_btn);
		btn.setOnClickListener((OnClickListener) this);
		
		emotion_btn_ = (ImageView)findViewById(R.id.emotion_btn);
		findViewById(R.id.emotion_rl).setOnClickListener(this);
		
		msgListView_ = (PullToRefreshListView) findViewById(R.id.chat_list);
    	sendEditText_ = (EditText) findViewById(R.id.sendmessage);
    	
    	other_input_sv_ = findViewById(R.id.other_input_sv);
    	
    	msgListView_.setMode(Mode.PULL_FROM_START);
    	msgListView_.setOnRefreshListener(this);

    	msgs_ = new ArrayList<ChatMsg>();
    	adapter_ = new ChatMsgViewAdapter(this, msgs_, this);
		getListView().setAdapter(adapter_);
		
		// 读取缓存的消息
		readMessage();

		sendEditText_.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				emotion_btn_.setImageResource(R.drawable.icon_chat_emote_normal);
				other_input_sv_.setVisibility(View.GONE);
			}
			
		});
		
		// 创建表情布局
		EmotionUtil.createEmotionLayout((TableLayout)findViewById(R.id.other_input_tl), sendEditText_);
	}

	@Override
	protected String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public String[] listNotificationInterests() {
		return new String[]{UserMsgProxy.SEND_MSG_COMPLETE,
				UserMsgProxy.SEND_MSG_FAILED,
				UserMsgProxy.GET_MSG_LIST_COMPLETE,
				UserMsgProxy.GET_MSG_LIST_FAILED,
				NetImageProxy.GET_IMAGE_COMPLETE};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		ReqData req = (ReqData)notification.getBody();

		if(noteName.equals(UserMsgProxy.SEND_MSG_COMPLETE))
		{
			String json = (String)req.output;
			ArrayList<ChatMsg> msgs = jsonToChatMsgs(json);

			insertMsgs((UserMsgInput)req.input, msgs);
			
			adapter_.notifyDataSetChanged();
			getListView().setSelection(getListView().getCount() - 1);

			// 保存消息
			saveMessage();
		}
		else if(noteName.equals(UserMsgProxy.SEND_MSG_FAILED))
		{
			setMsgSendFailed((UserMsgInput)req.input);
		}
		else if(noteName.equals(UserMsgProxy.GET_MSG_LIST_COMPLETE))
		{
			msgListView_.onRefreshComplete();
			
			String json = (String)req.output;

			ArrayList<ChatMsg> msgs = jsonToChatMsgs(json);
			
			int count = msgs.size();
			if(count == 0)
				return;

			UserMsgInput input = (UserMsgInput)req.input;
			int user_msg_id = input.user_msg_id;
			if(user_msg_id == 0)
			{
				for(int i = 0; i < msgs_.size();)
				{
					if(msgs_.get(i).state != SendState.Finished)
						break;
					
					msgs_.remove(i);
				}
				
				for(int i = count - 1; i >= 0; i--)
					msgs_.add(0, msgs.get(i));

				int startIndex = 0;
				int endIndex = msgs.size() - 1;
				
				// 分组显示时间
				groupMsgTime(startIndex, endIndex);
				
				//msgs_.clear();
				//msgs_.addAll(msgs);
			}
			else if(user_msg_id > 0)
			{
				insertMsgs((UserMsgInput)req.input, msgs);
			}
			else
			{
				for(int i = count - 1; i >= 0; i--)
					msgs_.add(0, msgs.get(i));

				int startIndex = msgs.size() - 1;
				int endIndex = 0;
				
				// 分组显示时间
				groupMsgTime(startIndex, endIndex);
				
				adapter_.notifyDataSetChanged();
				getListView().setSelectionFromTop(count + 1, 180);
                
				return;
			}
			
			adapter_.notifyDataSetChanged();
			getListView().setSelection(getListView().getCount() - 1);
			
			// 保存消息
			saveMessage();
		}
		else if(noteName.equals(UserMsgProxy.GET_MSG_LIST_FAILED))
		{
			msgListView_.onRefreshComplete();
		}
		else if(noteName.equals(NetImageProxy.GET_IMAGE_COMPLETE))
		{
			InputData input = (InputData)req.input;
			OutputData output = (OutputData)req.output;
			
			output.bitmap = ImageUtil.decodingBytes(output.data);

			if(output.bitmap != null)
			{
    			// 缓存起来
    			AppConfig.cache.setImage(input.key, output.bitmap, output.data);
    			
    			tAvatarURI_ = AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(avatar_path_));
			}
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		
		ListView listView = refreshView.getRefreshableView();
		
		String label = DateUtils.formatDateTime(listView.getContext().getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel 
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		if(refreshView.getCurrentMode() == Mode.PULL_FROM_START)
		{
			UserMsgInput input = new UserMsgInput();
			input.user_id = user_id_;
			input.latest = false;
			
			if(msgs_ == null || msgs_.size() == 0)
				input.user_msg_id = 0;
			else
				input.user_msg_id = -msgs_.get(0).id;

			ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
			req.input = input;
			
			sendNotification(UserMsgCmd.GET_USER_MSG_LIST, req);
		}
	}

	// 获取最新消息
	public void getLatestMsg()
	{
		UserMsgInput input = new UserMsgInput();
		input.user_id = user_id_;
		input.latest = true;
		
		input.user_msg_id = getVerifiedMsgId();

		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = input;
		
		sendNotification(UserMsgCmd.GET_USER_MSG_LIST, req);
	}
	
	// 当前activity是否在顶部
	public boolean isTop()
	{
		return isTop_;
	}
	
	// 获取当前的聊天用户id
	public int getChatUserID()
	{
		return user_id_;
	}
	
	private ListView getListView()
	{
		return msgListView_.getRefreshableView();
	}

	// 获取校验过的信息ID
	private int getVerifiedMsgId()
	{
		int id = 0;
		
		for(int i = msgs_.size() - 1; i >= 0; i--)
		{
			if(msgs_.get(i).state == SendState.Finished)
			{
				id = msgs_.get(i).id;
				break;
			}
		}
		
		return id;
	}
	
	// 添加最新的从服务器获取的评论
	private void insertMsgs(UserMsgInput input, ArrayList<ChatMsg> msgs)
	{
		int pos = msgs_.size() - 1;
		for(; pos >= 0; pos--)
		{
			if(msgs_.get(pos).state == SendState.Finished)
				break;
			
			// 找到当前正在发送的那条信息，并删除
			if(msgs_.get(pos).sending_time == input.sending_time)
			{
				msgs_.remove(pos);
			}
		}
		
		int startIndex = pos + 1;
		int endIndex = pos + msgs.size();
		
		if(input.user_msg_id == 0)
		{
			for(int i = 0; i < msgs.size(); i++)
			{
				msgs_.add(++pos, msgs.get(i));
			}
		}
		else
		{
			for(int i = msgs.size() - 1; i >= 0; i--)
			{
				msgs_.add(++pos, msgs.get(i));
			}
		}
		
		// 分组显示时间
		groupMsgTime(startIndex, endIndex);
	}

	// 设置某条信息发送不成功
	private void setMsgSendFailed(UserMsgInput input)
	{
		for(int i = msgs_.size() - 1; i >= 0; i--)
		{
			if(msgs_.get(i).state == SendState.Finished)
				break;
			
			if(msgs_.get(i).sending_time == input.sending_time)
			{
				msgs_.get(i).state = SendState.Failed;
				adapter_.notifyDataSetChanged();
				break;
			}
		}
	}
	
	// 分组显示时间
	private void groupMsgTime(int startIndex, int endIndex)
	{
		final long groupTime = 1800000;
		
		// 纠正索引不超出项数
		if(startIndex < 0)
			startIndex = 0;
		if(endIndex >= msgs_.size())
			endIndex = msgs_.size() - 1;
		
		if(msgs_.size() <= 1)
			return;
		
		// 显示最新信息的情况
		if(startIndex <= endIndex)
		{
    		int index = startIndex;
	    	while(index >= 0)
	    	{
	    		if(msgs_.get(index--).show_time)
	    			break;
	    	}
	    	
	    	long last_msg_group_time = 0;
	    	if(index >= 0)
	    		last_msg_group_time = StringUtil.parseDate(msgs_.get(index).date);
    		
    		for(index = startIndex; index <= endIndex; index++)
    		{
	    		ChatMsg msg = msgs_.get(index);
	    		
	            long time = StringUtil.parseDate(msg.date);

	            if(time - last_msg_group_time < groupTime)
	            	msg.show_time = false;
	            else
	            	last_msg_group_time = time;
    		}
    	}
		else
		{
    		int index = startIndex;
	    	while(++index < msgs_.size())
	    	{
	    		if(msgs_.get(index).show_time)
	    			break;
	    	}
	    	
	    	long last_msg_group_time = 0;
	    	if(index < msgs_.size())
	    		last_msg_group_time = StringUtil.parseDate(msgs_.get(index).date);
	    	else
	    		last_msg_group_time = StringUtil.parseDate(msgs_.get(msgs_.size() - 1).date) + groupTime;
    		
    		for(index = startIndex; index >= endIndex; index--)
    		{
	    		ChatMsg msg = msgs_.get(index);
	    		
	            long time = StringUtil.parseDate(msg.date);

	            if(last_msg_group_time - time < groupTime)
	            	msg.show_time = false;
	            else
	            	last_msg_group_time = time;
    		}
		}
	}
	
	// 保存账户信息
	public void saveMessage()
	{
		if(AppConfig.account_dir != null)
		{
			String json = ChatMsgsToJSON(msgs_, 10);
			SDCardUtil.writeFile(AccountUtil.getCurrAccountDir() + "c" + user_id_ + ".dat", json.getBytes());
		}
	}
	
	// 读取账户信息
	public void readMessage()
	{
		byte[] bytes = SDCardUtil.readFile(AccountUtil.getCurrAccountDir() + "c" + user_id_ + ".dat");
		if(bytes != null)
		{
			ArrayList<ChatMsg> msgs = jsonToChatMsgs(new String(bytes));

			msgs_.clear();
			msgs_.addAll(msgs);

			int startIndex = 0;
			int endIndex = msgs.size() - 1;
			
			// 分组显示时间
			groupMsgTime(startIndex, endIndex);
			
	    	adapter_.notifyDataSetChanged();

			getListView().setSelection(getListView().getCount() - 1);
		}
	}

	public String ChatMsgsToJSON(ArrayList<ChatMsg> msgs, int maxCount)
	{
		String json = "";

    	try
    	{
    		JSONArray arr = new JSONArray();
    		
    		int count = maxCount;
    		if(count > msgs.size())
    			count = msgs.size();
    		
    		int end = msgs.size() - count;
    		for(int i = end + count - 1; i >= end; i--)
    		{
	            JSONObject obj = new JSONObject();

                ChatMsg msg = msgs.get(i);
				
                int state = 0;
                switch(msg.state)
                {
                case Finished:
                	state = 0;
                	break;
                case Sending:
                	state = 2;
                	break;
                case Failed:
                	state = 2;
                	break;
                }
                
	            obj.put("state", state);
	            obj.put("st", msg.sending_time);
	            
	            obj.put("id", msg.id);

	            obj.put("cm", msg.isComMeg);
	            obj.put("time", msg.date);
	            
	            obj.put("msg", msg.text);

	        	arr.put(obj);
    		}
        	
            json = arr.toString();
        } catch (Exception e) {
        	json = "";
		}

    	return json;
	}
	
	public ArrayList<ChatMsg> jsonToChatMsgs(String json)
	{
		ArrayList<ChatMsg> msgs = new ArrayList<ChatMsg>();
		
    	try
    	{
			JSONArray arr = new JSONArray(json);

			int count = arr.length();
    		for(int i = 0; i < count; i++)
    		{
                JSONObject obj = (JSONObject)arr.get(count - i - 1);
		        
                ChatMsg msg = new ChatMsg();

                if(obj.has("state"))
                {
	                int state = obj.getInt("state");
	                switch(state)
	                {
	                case 0:
	                	msg.state = SendState.Finished;
	                	break;
	                case 1:
	                	msg.state = SendState.Sending;
	                	break;
	                default:
	                	msg.state = SendState.Failed;
	                	break;
	                }
                }
                else
            	{
                	msg.state = SendState.Finished;
            	}

                if(obj.has("st"))
                	msg.sending_time = obj.getLong("st");
                else
                	msg.sending_time = 0;
                
                msg.id = obj.getInt("id");
                
                if(obj.has("cm"))
                	msg.isComMeg = obj.getBoolean("cm");
                else
                	msg.isComMeg = obj.getInt("t") == AppConfig.account.info.user_id;
	            
	            msg.date = obj.getString("time");
	            msg.show_time = true;
	            
	            if(msg.isComMeg)
		            msg.avatar = tAvatarURI_;
	            else
		            msg.avatar = mAvatarURI_;
	            
	            msg.text = obj.getString("msg");
	            
                msgs.add(msg);
    		}
        } catch (Exception e) {
        	msgs = new ArrayList<ChatMsg>();
		}

    	return msgs;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);

		if(!facade.hasMediator(NAME))
			facade.registerMediator(this);
		
		isTop_ = true;
		
		getLatestMsg();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);

		if(facade.hasMediator(NAME))
			facade.removeMediator(NAME);
		
		isTop_ = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// 保存消息
		saveMessage();
	}

	private void sendMessage()
	{
		contString_ = sendEditText_.getText().toString().trim();
		if (contString_.length() > 0)
		{
			if(contString_.length() > 1000)
			{
				Toast.makeText(this, "字数要求不超过1000字", Toast.LENGTH_SHORT).show();
				return;
			}
			
			sendEditText_.setText("");

            ChatMsg msg = new ChatMsg();
			
            msg.state = SendState.Sending;
    		msg.sending_time = System.currentTimeMillis();
    		
            msg.id = 0;
            
            msg.isComMeg = false;
            msg.date = "";

            msg.avatar = mAvatarURI_;
            
            msg.text = contString_;
            
            msgs_.add(msg);
			adapter_.notifyDataSetChanged();
			
			getListView().setSelection(getListView().getCount() - 1);
			
			// 发送到服务器
			sendMsgImpl(contString_, msg.sending_time);
		}
	}

	private void sendMsgImpl(String content, long sending_time)
	{
		// 发送到服务器端
		UserMsgInput input = new UserMsgInput();

		input.sending_time = sending_time;
		
		input.user_id = user_id_;
		input.msg = content;
		input.user_msg_id = getVerifiedMsgId();

		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = input;
		
		sendNotification(UserMsgCmd.SEND_USER_MSG, req);
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.back_btn:
			finish();
			break;
		case R.id.send_btn:
			sendMessage();
			break;
		case R.id.emotion_rl:
			
			showEmotion(other_input_sv_.getVisibility() == View.GONE);
			break;
		}
	}

	private void showEmotion(boolean show)
	{
		if(show)
		{
			emotion_btn_.setImageResource(R.drawable.icon_chat_keyboard_normal);
			other_input_sv_.setVisibility(View.VISIBLE);
			
			// 隐藏输入法
			imm_.hideSoftInputFromWindow(other_input_sv_.getWindowToken(), 0);
		}
		else
		{
			emotion_btn_.setImageResource(R.drawable.icon_chat_emote_normal);
			other_input_sv_.setVisibility(View.GONE);

			// 显示输入法
			imm_.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}
	}
	
	@Override
	public void onAvatarClick(ChatMsg msg) {
		if(msg == null)
			return;

		Intent intent;
		
        if(!msg.isComMeg)
        {
			intent = new Intent(this, UserCenterSelfActivity.class);
		}
		else
		{
			intent = new Intent(this, UserCenterOtherActivity.class);

			intent.putExtra(AppConfig.INTENT_USER_ID, user_id_);
			intent.putExtra(AppConfig.INTENT_USER_NAME, username_);
		}
        
        startActivity(intent);
	}

	@Override
	public void onContentClick(ChatMsg msg) {
		if(msg == null)
			return;

		final ChatMsg cmsg = msg;
		
		if(msg.state == SendState.Failed)
		{
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setItems(new String[] {"重新发送","删除信息"}, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					switch(which)
					{
					case 0:
						cmsg.state = SendState.Sending;
						sendMsgImpl(cmsg.text, cmsg.sending_time);
						adapter_.notifyDataSetChanged();
						break;
					case 1:
						msgs_.remove(cmsg);
						adapter_.notifyDataSetChanged();
						break;
					}
				}
				
			})
			.create();

			dlg.show();
			dlg.setCanceledOnTouchOutside(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(imm_.isActive(other_input_sv_))
			{
				emotion_btn_.setImageResource(R.drawable.icon_chat_emote_normal);
				imm_.hideSoftInputFromWindow(other_input_sv_.getWindowToken(), 0);
				return true;
			}

			if(other_input_sv_.getVisibility() == View.VISIBLE)
			{
				emotion_btn_.setImageResource(R.drawable.icon_chat_emote_normal);
				other_input_sv_.setVisibility(View.GONE);
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
