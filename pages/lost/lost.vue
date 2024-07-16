<template>
  <view class="forum">
    <view v-for="(r, index) in info" :key="index" class="post">
      <view class="post-header">
        <view class="post-title">{{ r.title }}</view>
 
		<view class="post-date">{{ r.time }}</view>
      </view>
      <view class="post-author">
        <image src="/static/user-icon.png" class="user-icon"></image>
        <view class="username">{{ r.username }}</view>
 <!-- 将价格信息放置在作者用户名右侧 -->
      </view>
	  <view class="line">
	  	<view class="line-content">
	  		<image :src="r.avator" mode="widthFix" style="width: 180rpx;"></image>
	  	</view>
	  </view>
      <view class="post-content" >{{ r.content }}</view>
	  <button class="want-trade-button" @click="wantToTrade(r)">想要认领</button>
    </view>
    <button class="submit-button" @click="submitPost">发布物品</button>
	
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
      info: [], 
	  user: uni.getStorageSync('xm-user'),
	  replies: [],
	  orders: {},
	  user: uni.getStorageSync('xm-user'),
	  form: { price: 1, type: '' },
	  pickAddress: {},
	  recieveAddress: {},
	  tid:0
	};
  },
  onLoad() {
    this.load();
	 uni.$on('postAdded', this.load);
	 let orderStore = uni.getStorageSync('orderStore')
	 this.form.type = orderStore?.type
	 this.pickAddress = orderStore?.pickAddress || {}
	 
  },
  methods: {
    load() {
      this.$request.get('/lost/selectAll').then(res => {
        this.info = res.data || [];
      });
    },
    submitPost() {
			uni.navigateTo({
        url: '/pages/lost_post/lost_post'
      });
    },
	wantToTrade(r){
		this.tid=r.id
		uni.navigateTo({
		 	url: '/pages/talk/talk?tid='+this.tid
		 })
		// uni.navigateTo({
		// 	url: '/pages/taddress/taddress'
		// })
		// r.targetId=this.pickAddress.id
		// r.acceptId=this.user.id
		// this.$request.put('/transaction/update', r).then(() => {
		// 	uni.removeStorageSync('orderStore');
		// }).catch(error => {
		//   console.error('失败', error);
		// });
	
		
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
.post-weight {
  font-size: 12px;
 
}

.post-content {
  margin-top: 20rpx;
  margin-bottom: 10rpx;
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

.post-price {
  font-size: 20px; /* 设置价格信息的字体大小 */
  color: #ff9900; /* 设置价格信息的文本颜色 */
  margin-top: 1px; /* 设置价格信息与其他帖子信息之间的上边距 */
   margin-left: auto; 
}

want-trade-button {
  position: absolute;
   margin-top: 5px ;
  bottom: 10px;
  right: 10px;
  background-color: #007aff;
  color: deepskyblue;
  border: none;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

/* 其他样式 */
.post-author {
  display: flex;
  align-items: center;
}

.username {
  color: #333;
  margin-right: 10px;
}



</style>
</style>
