package com.yiyouapp;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.puremvc.java.interfaces.INotification;

import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.yiyouapp.adapters.CommentViewAdapter;
import com.yiyouapp.adapters.CommentViewAdapter.ICommentAction;
import com.yiyouapp.cmd.GetImageCmd;
import com.yiyouapp.cmd.RegCmd;
import com.yiyouapp.cmd.UserMsgCmd;
import com.yiyouapp.cmd.WorkCmd;
import com.yiyouapp.cmd.WorkCommentCmd;
import com.yiyouapp.cmd.WorkInfoCmd;
import com.yiyouapp.controls.TouchImageView;
import com.yiyouapp.proxy.LoginProxy;
import com.yiyouapp.proxy.NetImageProxy;
import com.yiyouapp.proxy.UserMsgProxy;
import com.yiyouapp.proxy.NetImageProxy.ImageType;
import com.yiyouapp.proxy.NetImageProxy.InputData;
import com.yiyouapp.proxy.NetImageProxy.OutputData;
import com.yiyouapp.proxy.UserMsgProxy.UserMsgInput;
import com.yiyouapp.proxy.WorkCommentProxy;
import com.yiyouapp.proxy.WorkCommentProxy.WorkCommentInput;
import com.yiyouapp.proxy.WorkListProxy;
import com.yiyouapp.proxy.WorkListProxy.WorksList;
import com.yiyouapp.proxy.WorkListProxy.WorksListInput;
import com.yiyouapp.proxy.WorkProxy;
import com.yiyouapp.utils.AccountUtil;
import com.yiyouapp.utils.EmotionUtil;
import com.yiyouapp.utils.ImageUtil;
import com.yiyouapp.utils.SizeUtil;
import com.yiyouapp.utils.StringUtil;
import com.yiyouapp.utils.WorkUtil;
import com.yiyouapp.value.CommentMsg;
import com.yiyouapp.value.CommentMsg;
import com.yiyouapp.value.SendState;
import com.yiyouapp.value.ReqData;
import com.yiyouapp.value.Work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class WorkCommentActivity extends MediatorActivity implements  OnRefreshListener<ListView>, OnClickListener, ICommentAction {

	public static final String NAME = "WorkCommentActivity";

	private static final int LOAD_THUMBS = 1000;
	
    private boolean isTop_;

	private EditText sendEditText_;
	private PullToRefreshListView commentListView_;
	private CommentViewAdapter adapter_;

	private TouchImageView workImage_;
	
	private Work work_;

    private int work_id_ = 0;
    private int reply_user_id_ = 0;// 回复的用户id
	
    private ArrayList<CommentMsg> comment_;
    private String contString_;
    
	private HashMap<Integer, String> nameMap_ = new HashMap<Integer, String>();

	// 消息处理
    private Handler msg_handler_ = new Handler()
    {
        public void handleMessage(Message msg) 
        {
        	super.handleMessage(msg);
        	
            switch(msg.what)
            {
            case LOAD_THUMBS:
            	loadThumbs();
            	break;
            }
        };
    };

    private View comment_none_rl_;
    
    private View other_input_sv_;
    private ImageView emotion_btn_;
    private InputMethodManager imm_;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_comment);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		imm_ = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		Button btn = (Button)findViewById(R.id.back_btn);
		btn.setOnClickListener((OnClickListener) this);

		btn = (Button)findViewById(R.id.send_btn);
		btn.setOnClickListener((OnClickListener) this);

		commentListView_ = (PullToRefreshListView) findViewById(R.id.comment_list);
    	sendEditText_ = (EditText) findViewById(R.id.sendmessage);
    	
    	workImage_ = (TouchImageView) findViewById(R.id.work_image);

		emotion_btn_ = (ImageView)findViewById(R.id.emotion_btn);
		findViewById(R.id.emotion_rl).setOnClickListener(this);
		
    	other_input_sv_ = findViewById(R.id.other_input_sv);
    	
    	commentListView_.setMode(Mode.DISABLED);
    	commentListView_.setOnRefreshListener(this);
    	
    	comment_ = new ArrayList<CommentMsg>();
    	adapter_ = new CommentViewAdapter(this, comment_, this);
		getListView().setAdapter(adapter_);

    	comment_none_rl_ = adapter_.getTopLine().findViewById(R.id.comment_none_rl);
    	
		adapter_.getTopLine().findViewById(R.id.work_thumb).setOnClickListener((OnClickListener) this);
		adapter_.getTopLine().findViewById(R.id.user_avatar).setOnClickListener((OnClickListener) this);
		workImage_.setOnClickListener((OnClickListener) this);
        
        getListView().setOnScrollListener(new OnScrollListener(){
    		
    		@Override
    	    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	        switch (scrollState) {
    	            case OnScrollListener.SCROLL_STATE_IDLE:
    	            	loadThumbs();
    	                break;
    	            case OnScrollListener.SCROLL_STATE_FLING:
    	                break;
    	            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
    	                break;
    	            }
    	    }
    	  
    	    @Override
    	    public void onScroll(AbsListView view, int firstVisibleItem,
    	           int visibleItemCount, int totalItemCount) {
    	    }
    	});

        // 初始化加载显示
        initLoading((ViewGroup)findViewById(R.id.root_container), null);

		// 设置正在加载
		setCommentLoading(1);
		
		// 获取intent传入数据
		Bundle bundle = getIntent().getExtras();
		Serializable ser = bundle.getSerializable("work");
		
		work_ = (Work)ser;
		
		if(work_ != null)
		{
			work_id_ = work_.works_id;
			showWork(work_);
		}
		else
		{
			work_id_ = getIntent().getIntExtra(AppConfig.INTENT_WORK_ID, 0);

			// 获取作品信息
			ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
			req.input = work_id_;
			
			sendNotification(WorkCmd.GET_WORK, req);
		}

		sendEditText_.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				emotion_btn_.setImageResource(R.drawable.icon_chat_emote_normal);
				other_input_sv_.setVisibility(View.GONE);
			}
			
		});
		
		sendEditText_.requestFocus();
		
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
		return new String[]{WorkProxy.GET_WORK_COMPLETE,
				WorkProxy.GET_WORK_FAILED,
				WorkCommentProxy.SEND_COMMENT_COMPLETE,
				WorkCommentProxy.SEND_COMMENT_FAILED,
				WorkCommentProxy.GET_COMMENT_LIST_COMPLETE,
				WorkCommentProxy.GET_COMMENT_LIST_FAILED,
				NetImageProxy.GET_IMAGE_COMPLETE,
				NetImageProxy.GET_IMAGE_FAILED};
	}

	@Override
	public void handleNotification(INotification notification) {
		String noteName = notification.getName();
		ReqData req = (ReqData)notification.getBody();

		if(noteName.equals(WorkCommentProxy.SEND_COMMENT_COMPLETE))
		{
			String json = (String)req.output;

			// 设置加载完成
			setCommentLoading(0);
			
			ArrayList<CommentMsg> msgs = jsonToCommentMsgs(json);

			insertComments((WorkCommentInput)req.input, msgs);
			
			adapter_.notifyDataSetChanged();
			loadThumbs();
			
			getListView().setSelection(getListView().getCount() - 1);
			
			// 无评论时的显示
			setCommentNone(adapter_.getCount() <= 1);
		}
		else if(noteName.equals(WorkCommentProxy.SEND_COMMENT_FAILED))
		{
			// 设置加载完成
			setCommentLoading(0);
			
			setCommentSendFailed((WorkCommentInput)req.input);
		}
		else if(noteName.equals(WorkCommentProxy.GET_COMMENT_LIST_COMPLETE))
		{
			commentListView_.onRefreshComplete();

			String json = (String)req.output;

			ArrayList<CommentMsg> msgs = jsonToCommentMsgs(json);
			
			int count = msgs.size();
			if(count == 0)
			{
				// 设置加载完成
				setCommentLoading(0);

				// 无评论时的显示
				setCommentNone(adapter_.getCount() <= 1);
				
				return;
			}

			// 无评论时的显示
			setCommentNone(false);
			
			WorkCommentInput input = (WorkCommentInput)req.input;
			int work_comment_id = input.work_comment_id;
			if(work_comment_id == 0)
			{
				// 设置加载完成
				setCommentLoading(0);
				
				comment_.clear();
				insertComments(input, msgs);
				adapter_.notifyDataSetChanged();
				setListViewPullEnable();
				
				Message message = Message.obtain();
				
    			message.obj = req;
    			message.what = LOAD_THUMBS;
    			
    			msg_handler_.sendMessage(message);
    			
				getListView().setSelection(0);//getListView().getCount() - 1);
		        return;
			}
			else if(work_comment_id > 0)
			{
				insertComments((WorkCommentInput)req.input, msgs);
				
				setListViewPullEnable();
			}
			else
			{
				for(int i = count - 1; i >= 0; i--)
					comment_.add(0, msgs.get(i));
				adapter_.notifyDataSetChanged();
				
				setListViewPullEnable();
				return;
			}
			
			adapter_.notifyDataSetChanged();
			loadThumbs();
			getListView().setSelection(getListView().getCount() - 1);
		}
		else if(noteName.equals(WorkCommentProxy.GET_COMMENT_LIST_FAILED))
		{
			// 设置加载错误
			setCommentLoading(2);
			// 无评论时的显示
			setCommentNone(false);
			
			commentListView_.onRefreshComplete();
		}
		else if(noteName.equals(WorkProxy.GET_WORK_COMPLETE))
		{
			work_ = (Work)req.output;

			work_id_ = work_.works_id;
			showWork(work_);
		}
		else if(noteName.equals(WorkProxy.GET_WORK_FAILED))
		{
			
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

    			if(input.position < 0)
    			{
	    	        ImageView iv;
	    	        
	    	        switch(input.type)
	    	        {
	    	        case Avatar:
	    	        	iv = (ImageView)adapter_.getTopLine().findViewById(R.id.user_avatar);
	    	        	iv.setImageBitmap(output.bitmap);
	    	        	break;
	    	        case Thumbnail:
	    	        	iv = (ImageView)adapter_.getTopLine().findViewById(R.id.work_thumb);
	    	        	iv.setImageBitmap(output.bitmap);
	    	        	break;
	    	        case BitImage:
	    	        	loading_.setCompleted();
	    	        	workImage_.setImageBitmap(output.bitmap);
	    	        	break;
	    	        }
    			}
    			else
    			{
    				adapter_.notifyDataSetChanged();
    			}
			}
		}
		else if(noteName.equals(NetImageProxy.GET_IMAGE_FAILED))
		{
			InputData input = (InputData)req.input;
			OutputData output = (OutputData)req.output;

			if(input.position < 0)
			{
    	        switch(input.type)
    	        {
    	        case Avatar:
    	        	break;
    	        case Thumbnail:
    	        	break;
    	        case BitImage:
    	        	loading_.setCompleted();
    	        	break;
    	        }
			}
		}
	}

	// 启用 listview 上拉
	private void setListViewPullEnable()
	{
		if(adapter_.getCount() >= 45)
	    	commentListView_.setMode(Mode.PULL_FROM_END);
		else
	    	commentListView_.setMode(Mode.DISABLED);
	}
	
	// 设置评论加载结果的显示
	private void setCommentLoading(int code)
	{
		View top_line = adapter_.getTopLine();
		View comment_loading = top_line.findViewById(R.id.comment_loading_rl);
		View progress_bar = top_line.findViewById(R.id.progressBar);
		TextView loading_text = (TextView)top_line.findViewById(R.id.loading_text);
		
		if(code == 0)
		{
			if(comment_loading.getVisibility() == View.VISIBLE)
				comment_loading.setVisibility(View.GONE);
		}
		else if(code == 1)
		{
			comment_loading.setVisibility(View.VISIBLE);
			progress_bar.setVisibility(View.VISIBLE);
			loading_text.setText("  正在加载...");
		}
		else
		{
			comment_loading.setVisibility(View.VISIBLE);
			progress_bar.setVisibility(View.GONE);
			loading_text.setText("很抱歉，当前您的网络加载失败");
		}
	}
	
	private void showWork(Work work)
	{
		View top = adapter_.getTopLine();
	
		top.findViewById(R.id.work_info_rl).setVisibility(View.VISIBLE);
		
		// 需要加载的图片列表
		ArrayList<ReqData> reqList = new ArrayList<ReqData>();
		
        //View userInfoRL = top.findViewById(R.id.user_info_rl);
        
        ImageView workThumb = (ImageView)top.findViewById(R.id.work_thumb);
        TextView workDesc = (TextView)top.findViewById(R.id.work_desc);

        ImageView userAvatar = (ImageView)top.findViewById(R.id.user_avatar);
        TextView uploadTime = (TextView)top.findViewById(R.id.upload_time);

        ImageView sexType = (ImageView)top.findViewById(R.id.sex_type);
        TextView userType = (TextView)top.findViewById(R.id.user_type);
        TextView userName = (TextView)top.findViewById(R.id.user_name);

        TextView userLocation = (TextView)top.findViewById(R.id.user_location);
        TextView userDistance = (TextView)top.findViewById(R.id.user_distance);

        workDesc.setText(work.work_desc);
        userName.setText(work.user_name);

        int secs = (int)(new Date(System.currentTimeMillis()).getTime() - work.device_time.getTime()) / 1000;
        if(secs < 0)
        	secs = 0;
        uploadTime.setText(WorkUtil.getTimeDesc(work.elapsed_secs + secs));

        if(work.sex.equals("M"))
        	sexType.setImageResource(R.drawable.icon_male);
        else
        	sexType.setImageResource(R.drawable.icon_female);
        
        userType.setText(WorkUtil.getUserTypeNameFromValue(work.user_type));

        if(work.work_desc.equals(""))
        	workDesc.setVisibility(View.GONE);
        else
        	workDesc.setVisibility(View.VISIBLE);
        
        userLocation.setText(work.city);
        if(work.has_location)
        {
        	userDistance.setVisibility(View.VISIBLE);
        	userDistance.setText(StringUtil.formatDistance(work.distance));
        }
        else
        {
        	userDistance.setVisibility(View.GONE);
        }

        LayoutParams para;
        para = workThumb.getLayoutParams();

        para.width = (int)((float)200 * work.thumb1_ratio);
        para.height = 200;
        workThumb.setLayoutParams(para);

        String fileName = StringUtil.getFileNameFromPath(work.thumb_path[0]);
        Bitmap thumbImage = AppConfig.cache.mem.getImage(fileName);
        if(thumbImage != null)
        {
        	workThumb.setImageBitmap(thumbImage);
        }
        else
        {
        	workThumb.setImageBitmap(null);
        	
        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(fileName);
        	if(uri != null)
        	{
        		workThumb.setImageURI(uri);
        	}
        	else
        	{
        		// 去加载缩略图

				ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
				InputData input = new InputData();
				
				input.url = StringUtil.getFileUrl(work.thumb_path[0]);
				input.cachable = true;
				input.type = ImageType.Thumbnail;
				input.key = StringUtil.getFileNameFromPath(work.thumb_path[0]);
				input.position = -1;
				
				req.input = input;
				
				reqList.add(req);
        	}
        }
        
        fileName = StringUtil.getFileNameFromPath(work.avatar_path);
        Bitmap avatarImage = AppConfig.cache.mem.getImage(fileName);
        if(avatarImage != null)
        {
        	userAvatar.setImageBitmap(avatarImage);
        }
        else
        {
        	userAvatar.setImageBitmap(null);
        	
        	Uri uri = AppConfig.cache.sdcard.getExistFileUri(fileName);
        	if(uri != null)
        	{
        		userAvatar.setImageURI(uri);
        	}
        	else
        	{
                userAvatar.setImageResource(R.drawable.default_avatar_100);
                
                // 去加载头像
				ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
				InputData input = new InputData();
				
				input.url = StringUtil.getFileUrl(work.avatar_path);
				input.cachable = true;
				input.type = ImageType.Avatar;
				input.key = StringUtil.getFileNameFromPath(work.avatar_path);
				input.position = -1;
				
				req.input = input;
				
				reqList.add(req);
        	}
        }

        // 若是需要加载，发送加载请求
		if(reqList.size() > 0)
			sendNotification(GetImageCmd.GET_IMAGE, reqList);
	}
	
	// 获取校验过的评论ID
	private int getVerifiedCommentId()
	{
		int id = 0;
		
		for(int i = comment_.size() - 1; i >= 0; i--)
		{
			if(comment_.get(i).state == SendState.Finished)
			{
				id = comment_.get(i).id;
				break;
			}
		}
		
		return id;
	}
	
	// 添加最新的从服务器获取的评论
	private void insertComments(WorkCommentInput input, ArrayList<CommentMsg> msgs)
	{
		int pos = comment_.size() - 1;
		for(; pos >= 0; pos--)
		{
			if(comment_.get(pos).state == SendState.Finished)
				break;
			
			// 找到当前正在发送的那条信息，并删除
			if(comment_.get(pos).sending_time == input.sending_time)
			{
				comment_.remove(pos);
			}
		}

		for(int i = msgs.size() - 1; i >= 0; i--)
		{
			comment_.add(++pos, msgs.get(i));
		}
		
	}
	
	// 设置某条信息发送不成功
	private void setCommentSendFailed(WorkCommentInput input)
	{
		for(int i = comment_.size() - 1; i >= 0; i--)
		{
			if(comment_.get(i).state == SendState.Finished)
				break;
			
			if(comment_.get(i).sending_time == input.sending_time)
			{
				comment_.get(i).state = SendState.Failed;
				adapter_.notifyDataSetChanged();
				break;
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

		if(refreshView.getCurrentMode() == Mode.PULL_FROM_END)
		{
			WorkCommentInput input = new WorkCommentInput();
			
			input.work_id = work_id_;
			input.work_comment_id = getVerifiedCommentId();

			ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
			req.input = input;
			
			sendNotification(WorkCommentCmd.GET_COMMENT_LIST, req);
		}
	}

	// 获取最新消息
	public void getLatestMsg()
	{
		WorkCommentInput input = new WorkCommentInput();
		
		input.work_id = work_id_;
		input.work_comment_id = getVerifiedCommentId();

		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = input;
		
		sendNotification(WorkCommentCmd.GET_COMMENT_LIST, req);
	}
	
	// 当前activity是否在顶部
	public boolean isTop()
	{
		return isTop_;
	}
	
	// 获取当前的聊天用户id
	public int getWorkId()
	{
		return work_id_;
	}
	
	private ListView getListView()
	{
		return commentListView_.getRefreshableView();
	}
	
	public ArrayList<CommentMsg> jsonToCommentMsgs(String json)
	{
		ArrayList<CommentMsg> msgs = new ArrayList<CommentMsg>();
		
    	try
    	{
			JSONArray arr = new JSONArray(json);

			int count = arr.length();
    		for(int i = 0; i < count; i++)
    		{
                JSONObject obj = (JSONObject)arr.get(count - i - 1);
		        
                CommentMsg msg = new CommentMsg();

                // 从服务器端获取的，都是校验过的
                msg.state = SendState.Finished;
                
                msg.id = obj.getInt("id");
                
                msg.user_id = obj.getInt("user_id");
                msg.reply_user_id = obj.getInt("rid");

	            msg.name = obj.getString("name");
	            msg.date = obj.getString("time");
	            
            	msg.original_text = obj.getString("comment");

                msg.avatar_path = obj.getString("avatar_path").replace("*", "64");

                msgs.add(msg);
                
                if(!nameMap_.containsKey(msg.user_id))
                	nameMap_.put(msg.user_id, msg.name);
    		}
        } catch (Exception e) {
        	msgs = new ArrayList<CommentMsg>();
		}

		for(int i = 0; i < msgs.size(); i++)
		{
			CommentMsg msg = msgs.get(i);
			
	        if(msg.reply_user_id > 0 && nameMap_.containsKey(msg.reply_user_id))
	        	msg.text = "回复 " + nameMap_.get(msg.reply_user_id) + ": " + msg.original_text;
	        else
	        	msg.text = msg.original_text;
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

	private void sendComment()
	{
		if(work_ == null)
		{
			Toast.makeText(this, "抱歉，作品未加载完", Toast.LENGTH_SHORT).show();
			return;
		}
		
		contString_ = sendEditText_.getText().toString().trim();
		if (contString_.length() > 0)
		{
			if(contString_.length() > 1000)
			{
				Toast.makeText(this, "字数要求不超过1000字", Toast.LENGTH_SHORT).show();
				return;
			}
			
			sendEditText_.setText("");
			
			// 添加到消息列表
    		CommentMsg msg = new CommentMsg();

    		msg.state = SendState.Sending;
    		msg.sending_time = System.currentTimeMillis();
    		
            msg.id = 0;
            
    		msg.date = "";//new Date().toString();
    		msg.name = AppConfig.account.info.user_name;

            msg.user_id = AppConfig.account.info.user_id;
            msg.reply_user_id = reply_user_id_;

            msg.avatar_path = AppConfig.account.info.avatar64_path;
            
    		if(reply_user_id_ != 0)
    		{
    			msg.text = "回复 " + nameMap_.get(msg.reply_user_id) + ": " + contString_;
    		}
    		else
    		{
    			msg.text = contString_;
    		}
        	msg.original_text = contString_;
    		
			comment_.add(msg);
			adapter_.notifyDataSetChanged();
			
			getListView().setSelection(getListView().getCount() - 1);
			
			// 发送到服务器端
			sendCommentImpl(contString_, reply_user_id_, msg.sending_time);
			
			// 清空回复用户的id
			sendEditText_.setHint("");
			reply_user_id_ = 0;
		}
	}

	private void sendCommentImpl(String content, int reply_user_id, long sending_time)
	{
		// 发送到服务器端
		WorkCommentInput input = new WorkCommentInput();
		
		input.sending_time = sending_time;
		
		input.work_id = work_id_;
		input.work_comment_id = getVerifiedCommentId();
		input.rid = reply_user_id; // 回复的用户id
		input.user_id = work_.user_id;
		input.comment = content;

		ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
		req.input = input;
		
		sendNotification(WorkCommentCmd.SEND_COMMENT, req);
	}
	
	private void setCommentNone(boolean none)
	{
		if(none)
		{
			if(comment_none_rl_.getVisibility() == View.GONE)
				comment_none_rl_.setVisibility(View.VISIBLE);
		}
		else
		{
			if(comment_none_rl_.getVisibility() == View.VISIBLE)
				comment_none_rl_.setVisibility(View.GONE);
		}
	}
	
	public void loadThumbs()
	{
		ListView listView = getListView();
		
		int start = listView.getFirstVisiblePosition();
		int end = listView.getLastVisiblePosition();

		if(start <= end)
		{
			ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			
			for(int i = start; i <= end; i++)
			{
				CommentMsg msg = (CommentMsg) listView.getItemAtPosition(i);
				if(msg == null)
					continue;
				
				// thumb
				String key = StringUtil.getFileNameFromPath(msg.avatar_path);
				if(!AppConfig.cache.isExist(key))
				{
					ReqData req = ReqData.create(this, ReqData.FailHandleType.Quite);
					InputData input = new InputData();
					
					input.url = StringUtil.getFileUrl(msg.avatar_path);
					input.cachable = true;
					input.type = ImageType.Avatar;
					input.position = i;
					input.key = key;
					
					req.input = input;
					
					reqList.add(req);
				}
				
			}
	
			if(reqList.size() > 0)
				sendNotification(GetImageCmd.GET_IMAGE, reqList);
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
	public void onClick(View v) {
		
		switch(v.getId())
		{
		case R.id.back_btn:
			finish();
			break;
		case R.id.send_btn:
			sendComment();
			break;
		case R.id.emotion_rl:
			
			showEmotion(other_input_sv_.getVisibility() == View.GONE);
			break;
		case R.id.user_avatar:

			Intent intent;
			
	        if(work_.user_id == AppConfig.account.info.user_id)
	        {
				intent = new Intent(this, UserCenterSelfActivity.class);
			}
			else
			{
				intent = new Intent(this, UserCenterOtherActivity.class);

				intent.putExtra(AppConfig.INTENT_USER_ID, work_.user_id);
				intent.putExtra(AppConfig.INTENT_USER_NAME, work_.user_name);
			}
	        
	        startActivity(intent);
	        
	        break;
		case R.id.work_thumb:
			
			workImage_.setVisibility(View.VISIBLE);

			Uri uri = AppConfig.cache.sdcard.getExistFileUri(StringUtil.getFileNameFromPath(work_.file_path[0]));
			
			if(uri == null)
			{
				//ImageView workThumb = (ImageView)adapter_.getTopLine().findViewById(R.id.work_thumb);
				//workImage_.setImageDrawable(workThumb.getDrawable());
				workImage_.setBackgroundColor(0xFF000000);
				
				loading_.setLoading();
				
				ArrayList<ReqData> reqList = new ArrayList<ReqData>();
			
				String key = StringUtil.getFileNameFromPath(work_.file_path[0]);
			
				ReqData req = ReqData.create(this, ReqData.FailHandleType.Notify);
				InputData input = new InputData();
				
				input.url = StringUtil.getFileUrl(work_.file_path[0]);
				input.cachable = true;
				input.type = ImageType.BitImage;
				input.key = key;
				input.position = -1;
				
				req.input = input;
				
				reqList.add(req);
				
				sendNotification(GetImageCmd.GET_IMAGE, reqList);
			}
			else
			{
	        	workImage_.setImageURI(uri);
				workImage_.setBackgroundColor(0xFF000000);
			}
			
			break;
		case R.id.work_image:
			
			//workImage_.setVisibility(View.GONE);
			
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
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
			
			if(workImage_.getVisibility() == View.VISIBLE)
			{
				workImage_.setVisibility(View.GONE);

				return true;
			}
			
			// 检查是否还有未发送成功的评论

			if(comment_.size() > 0)
			{
				if(comment_.get(comment_.size() - 1).state != SendState.Finished)
				{
					new AlertDialog.Builder(this)
					.setMessage("您还有未发送成功的评论，确定放弃吗？")  
					.setPositiveButton("确定" , new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog,int id) {

							finish();
						}
					
					})  
					.setNegativeButton("取消" , null)
					.show();

					return true;
				}
			}
		}
		
		return super.onKeyDown(keyCode, event);                                
	}

	@Override
	public void onAvatarClick(CommentMsg msg) {
		if(msg == null)
			return;

		Intent intent;
		
        if(msg.user_id == AppConfig.account.info.user_id)
        {
			intent = new Intent(this, UserCenterSelfActivity.class);
		}
		else
		{
			intent = new Intent(this, UserCenterOtherActivity.class);

			intent.putExtra(AppConfig.INTENT_USER_ID, msg.user_id);
			intent.putExtra(AppConfig.INTENT_USER_NAME, msg.name);
		}
        
        startActivity(intent);
	}

	@Override
	public void onContentClick(CommentMsg msg) {
		if(msg == null)
			return;

		final CommentMsg cmsg = msg;
		
		if(msg.state == SendState.Failed)
		{
			AlertDialog dlg = new AlertDialog.Builder(this)
			.setItems(new String[] {"重新发送","删除评论"}, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					switch(which)
					{
					case 0:
						cmsg.state = SendState.Sending;
						sendCommentImpl(cmsg.original_text, cmsg.reply_user_id, cmsg.sending_time);
						adapter_.notifyDataSetChanged();
						break;
					case 1:
						comment_.remove(cmsg);
						adapter_.notifyDataSetChanged();
						break;
					}
				}
				
			})
			.create();

			dlg.show();
			dlg.setCanceledOnTouchOutside(true);
		}
		else if(msg.state == SendState.Sending)
		{
			return;
		}
		
		if((reply_user_id_ == 0 || reply_user_id_ != msg.user_id) &&
			AppConfig.account.info.user_id != msg.user_id)
		{
			reply_user_id_ = msg.user_id;
			
			sendEditText_.setHint("回复 " + msg.name);
		}
		else
		{
			sendEditText_.setHint("");
			
			reply_user_id_ = 0;
		}
	}

}
