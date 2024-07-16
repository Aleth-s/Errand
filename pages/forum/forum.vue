<template>
	
  <view class="forum">
    <!-- 显示帖子列表 -->
	<view class="box" style="margin-bottom: 8rpx;">
	    <uni-segmented-control :current="current" :values="items" @clickItem="onClickItem" styleType="text"
	        activeColor="#006eff"></uni-segmented-control>
	</view>
    <view v-for="(post, index) in posts" :key="index" class="post">
      <view class="post-header">
        <view class="post-title">{{ post.title }}</view>
        <view class="post-date">{{ post.time }}</view>
      </view>
      <view class="post-author">
        <image src="/static/user-icon.png" class="user-icon"></image>
        <view class="username">{{ post.userName }}</view>
		<view style="margin-left: 5px;">
		<uni-tag text="骑手" size="mini" type="success" v-if="post.role === 'rider'"></uni-tag> </view>
      </view>
      <view class="post-content" >{{ post.content }}</view>
      <view class="post-footer">
        <view class="post-reply" @click="showReplies(post)"></view>
      </view>
	  <view class="like-button" @click="post(post)">
	          <image :src="post.liked ? '/static/redzan.png' : '/static/zan.png'" class="like-icon"></image>
	          <view class="like-count">{{ post.otherinfo}}</view>
	</view>
    </view>
    <button class="submit-button" @click="submitPost">发布帖子</button>
	
	<view v-if="showReplyWindow" class="reply-window">
	      <view class="reply-overlay" @click="closeReplyWindow"></view>
	      <view class="reply-content">
	        <textarea v-model="replyContent" placeholder="请输入回复内容"></textarea>
	        <button @click="submitReply">提交回复</button>
	      </view>
	</view>
	
	<view v-if="showReplyInfo" class="reply-info">
	      <view class="reply-overlay" @click="closeReplyInfo"></view>
	     <view v-for="(reply, index) in replies" :key="index">
		  <view class="replyinfo-content">	        
	          <view class="post-title">{{ reply.content }}</view>
	          <view class="post-date">{{ reply.time }}</view>       
	      </view>
		  </view>
    </view>
		
  </view>
</template>

<script>
export default {
  data() {
    return {
      posts: [], // 存储帖子列表
      newPost: { title: '', content: '' }, // 新帖子的信息
	  showReplyWindow: false, // 控制回复窗口的显示
	  showReplyInfo: false, // 控制回复信息窗口的显示
	  replyContent: '', // 存储用户输入的回复内容
	  selectedPost: null, // 存储用户选择回复的帖子
	  user: uni.getStorageSync('xm-user'),
	  replies: [] ,// 存储帖子的回复信息
	  
	  items: ['全部', '骑手', '普通用户'],
	  current: '全部',
	  role:''
	};
  },
  onLoad() {
    this.load();
	 uni.$on('postAdded', this.load);
  },
  methods: {
    load() {
		if (this.current == '全部') {
			this.$request.get('/forum/selectAll/').then(res => {
			  this.posts = res.data || [];
			});
		}
      else{
		  
		  this.$request.get('/forum/selectByRole/'+this.current).then(res => {
        this.posts = res.data || [];
      });
	  }
    },
	onClickItem(e) {
		
	    this.current = this.items[e.currentIndex]
	    this.load()
	},
    submitPost() {
			uni.navigateTo({
        url: '/pages/edit/edit'
      });
    },
    // 回复帖子
    reply(post) {
		this.selectedPost = post; 
		this.showReplyWindow = true;
      // 这里可以实现回复功能，根据需求决定如何实现
    },
	post(post){
		
			  
			  post.liked = !post.liked; // 切换点赞状态
			  
			        // 向后端发送点赞请求，并更新点赞数
			        if (post.liked) {
			         // 向后端发送点赞请求
			         	post.otherinfo++;
			            this.$request.put('/forum/update', post).then(() => {
			              // 请求成功后，更新页面上的点赞数
			               // 假设帖子对象中有一个属性用于存储点赞数
			            }).catch(error => {
			              console.error('点赞失败：', error);
			            });
			        } else {
			          // 如果当前已点赞，则发送取消点赞请求，并更新点赞数
			          post.otherinfo--;
			          this.$request.put('/forum/update', post).then(() => {
			            // 请求成功后，更新页面上的点赞数
			             // 假设帖子对象中有一个属性用于存储点赞数
			          }).catch(error => {
			            console.error('点赞失败：', error);
			          });
			        }
		},
	showReplies(post) {
	      this.selectedPost = post;
	      this.$request.get('/reply/selectAll', { params: { userId: post.id } }).then(res => {
	        this.replies = res.data || [];
	        this.showReplyInfo = true;
	      }).catch(error => {
	        console.error('获取回复信息失败：', error);
	      });
	    },
	closeReplyWindow() {
	      this.showReplyWindow = false;
	    },
	closeReplyInfo() {
		this.showReplyInfo = false;
		    },
	submitReply() {
		  this.form.replyUser = this.user.id
	      // 向后端发送回复内容
	      this.$request.post('/reply/add', {postId: this.selectedPost.id, // 假设帖子有唯一的 id 属性
        content: this.replyContent,
      }).then(() => {
	        // 提交成功后关闭回复窗口，并重新加载帖子列表
	        this.showReplyWindow = false;
	        this.load();
	      }).catch(error => {
	        console.error('提交回复失败：', error);
	      });
		  }
  }
};
</script>

<style>
.post-author {
  display: flex; /* 使用 Flexbox 布局 */
  align-items: center; /* 垂直居中 */
}
.user-icon {
  width: 20px;
  height: 20px;
  margin-right: 5px; /* 调整图标与用户名之间的间距 */
}

.username {
  color: #333; /* 可以根据需要调整用户名的颜色 */
 
}

.forum {
  padding: 20rpx;
}

.post {
  margin-bottom: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
  padding: 20rpx;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #007aff;
  margin-bottom: 10rpx;
}

.post-title {
  font-weight: bold;
  font-size: 18px;
  color: #333;
}

.post-date {
  font-size: 12px;
  color: #999;
}

.post-content {
  margin-top: 20rpx;
 
  color: #666;
}

.post-footer {
  display: flex;
  justify-content: flex-end;
}

.post-reply {
  color: #007aff;
  cursor: pointer;
}

.post-form textarea {
  width: 100%;
  margin-bottom: 10rpx;
  padding: 10rpx;
  border: 1px solid #ccc;
  border-radius: 5rpx;
}

.submit-button {
  background-color: rgba(0, 122, 255, 0.8); /* 使用 RGBA 颜色设置背景色，并指定透明度 */
  color: #fff;
  border: none;
  padding: 15rpx 80rpx; /* 调整按钮的上下内边距和左右内边距 */
  border-radius: 30rpx; /* 使用较大的值使按钮更圆润 */
  cursor: pointer;
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 16px;
}

.reply-window {
  position: fixed;
  top: 50%; /* 将回复窗口位置设为屏幕中心下方 */
  left: 50%;
  transform: translate(-50%, 0%);
  width: 100%;
  display: flex;
  justify-content: center;
}

.reply-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.5);
}

.reply-content {
  width: 70%; /* 调整回复窗口的宽度 */
  background-color: #fff; /* 修改回复窗口背景颜色为白色 */
  padding: 20rpx;
  border-radius: 10rpx;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.replyinfo-content {
  display: flex;
  background-color: #fff; /* 修改回复窗口背景颜色为白色 */
  justify-content: space-between;
  align-items: center;
  color: #007aff;
  margin-bottom: 10rpx;
}

.reply-content textarea {
  width: 100%;
  margin-bottom: 10rpx;
  padding: 10rpx;
  border: 1px solid #ccc;
  border-radius: 5rpx;
}

.reply-content button {
  background-color: rgba(0, 122, 255, 0.8);
  color: #fff;
  border: none;
  padding: 10rpx 20rpx;
  border-radius: 5rpx;
  cursor: pointer;
}

.reply-info {

  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%; /* 设置回复信息窗口宽度为屏幕的一半 */
  max-height: 90%; /* 设置回复信息窗口最大高度为屏幕的80% */
  overflow-y: auto; /* 添加滚动条以滚动长内容 */
  background-color: #fff; /* 设置背景颜色为白色 */
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.reply-info .reply-content .reply-item {
  margin-bottom: 10px; /* 设置回复项之间的间距 */
}

.post-footer {
  display: flex;
  justify-content: flex-end; /* 将按钮向右对齐 */
  align-items: center; /* 垂直居中 */
}

.like-button {
  display: flex;
  align-items: center; /* 垂直居中 */
  margin-right: 10px; /* 添加一些右边距 */
}

.like-icon {
  width: 17px;
  height: 17px;
}

.like-count {
  font-size: 14px;
}

.post-reply {
  color: #007aff;
  cursor: pointer;
}
</style>
</style>
