<template>
	<view class="chat">
    <!-- 顶部标题 -->  
		<scroll-view  :style="{height: `${windowHeight-inputHeight - 180}rpx`}"
		id="scrollview"
		scroll-y="true" 
		:scroll-top="scrollTop"
		class="scroll-view"
		>
			<view style="position: fixed; right: 20rpx; top: 20rpx;">
			     <image @click="refresh()" src="../../static/refresh.png"  style="width: 75rpx; height: 75rpx;"></image> 
				
			   </view>
			<view style="position: fixed; right: 20rpx; top: 100rpx;">
			     <image @click="exit()" src="../../static/tuichu.png"  style="width: 75rpx; height: 75rpx;"></image> 
				
			   </view>
			<!-- 聊天主体 -->
			<view id="msglistview" class="chat-body">
				<!-- 聊天记录 -->
				<view v-for="(item,index) in chatList" :key="index">
					<!-- 机器人发的消息 -->
					<view class="item Ai" v-if="item.chatUserId === user.id " >
						<!-- 头像 -->
						<image class="avatar" :src="item.userAvatar">
						</image>
						<!-- 文字内容 -->
						<view>
						<view style="color: #bl; margin-bottom: 5px; margin-left: 20px; ">
						客服
						</view>
						<view class="content left">	
						{{item.text}}
						</view>
						</view>
					</view>
					<!-- 人发的消息 -->
					<view class="item self" v-if="item.userId === user.id">
						<!-- 文字内容 -->
						<view class="content right">
							{{item.text}}
						</view>
						<!-- 头像 -->     
						<image class="avatar" :src="item.userAvatar">
						</image>
						
					</view>
				</view>
			</view>
		</scroll-view>
		<!-- 底部消息发送栏 -->
		<!-- 用来占位，防止聊天消息被发送框遮挡 -->
		<view class="chat-bottom" :style="{height: `${inputHeight}rpx`}">
			<view class="send-msg" :style="{bottom:`${keyboardHeight - 60}rpx`}">
        <view class="uni-textarea">
          <textarea v-model="chatMsg"
            maxlength="300"
            confirm-type="send"
            @confirm="handleSend"
            placeholder="快来聊天吧~"
            :show-confirm-bar="false"
            :adjust-position="false"
            @linechange="sendHeight"
            @focus="focus" @blur="blur"
           auto-height></textarea>
        </view>
				<button @click="handleSend" class="send-btn">发送</button>
			</view>
		</view>
	</view>
</template>
<script>
	let socket
	export default{
		data() {
			return {
				newChatList:[],
				adminId:4,
				user: uni.getStorageSync('xm-user'),
				chatGroupList: [],
				activeChatUserId: 4,
				form: {},
				chatList: [],
				//键盘高度
				keyboardHeight:0,
				//底部消息发送高度
				bottomHeight: 0,
				//滚动距离
				scrollTop: 0,
				userId:'',
				//发送的消息
				chatMsg:"",
				msgList:[
					{
					    botContent: "你好啊，很高兴你可以关注我，请问我有什么可以帮助你的吗？",
					    userContent: "",
              image:"/static/common/unname1.jpeg"
					},
					{
					    botContent: "",
					    userContent: "你好呀，非常高兴认识你",
              image:"/static/common/unname2.jpg"
					},
				]	
			}
		},
		updated(){
			//页面更新时调用聊天消息定位到最底部
			this.scrollToBottom();
		},
		computed: {
			windowHeight() {
			    return this.rpxTopx(uni.getSystemInfoSync().windowHeight)
			},
			// 键盘弹起来的高度+发送框高度
			inputHeight(){
				return this.bottomHeight+this.keyboardHeight
			}
		},
		onLoad(){
			
			let _this = this
			this.chat(this.adminId)
			this.loadChatGroup()
			this.loadChatList(this.activeChatUserId)
			
			
			
        uni.onKeyboardHeightChange(res => {
				//这里正常来讲代码直接写
				//this.keyboardHeight=this.rpxTopx(res.height)就行了
				//但是之前界面ui设计聊天框的高度有点高,为了不让键盘和聊天输入框之间距离差太大所以我改动了一下。
				this.keyboardHeight = this.rpxTopx(res.height)
				if(this.keyboardHeight<0)this.keyboardHeight = 0;
			})
			
			// 在页面或组件的方法中调用
			uni.connectSocket({
			  url: 'ws://localhost:9090/chatServer/1',
			  success() {
			    console.log('WebSocket连接成功');
			  },
			  fail(err) {
			    console.error('WebSocket连接失败', err);
			  }
			});
			uni.onSocketOpen(function () {
			  console.log('WebSocket连接已打开');
			});
			
			uni.onSocketMessage(function (res) {
				_this.loadChatGroup()
				_this.loadChatList(_this.activeChatUserId)
			  console.log('收到消息：', res.data);
			});
			
			uni.onSocketError(function (err) {
			  console.error('WebSocket错误：', err);
			});
			
		
				uni.onSocketClose(function (res) {
				  console.log('WebSocket 已关闭！');
				});

		},
		// onUnload(){
		// 	uni.onSocketClose(function (res) {
		// 	  console.log('WebSocket 已关闭！');
		// 	});
		// },
		methods: {
			refresh(){
					uni.showToast({
						icon: 'yes',
						title: '刷新成功'
					})
				    this.$request.get('/chatInfo/selectUserChat/' + this.activeChatUserId).then(newChatList => {  
				        // 更新数据  
				        this.chatList = newChatList.data || [] 
				        // 如果需要，可以滚动到最新消息的位置  
				        this.scrollToBottom();  
				      });  
			},
			exit(){
				uni.closeSocket({
				            success() {
				                console.log('WebSocket 已关闭');
				            },
				            fail(err) {
				                console.error('关闭 WebSocket 连接失败', err);
				            }
				        })
				uni.switchTab({
				  url: '/pages/personal/personal'
				});
			},
			chat(userId) {
			  this.$request.post('/chatGroup/add', { chatUserId: userId, userId: this.user.id }).then(res => {
			  })
			},
			loadChatGroup() {
			    this.$request.get('/chatGroup/selectUserGroup').then(res => {
			    this.chatGroupList = res.data || []
			    })
			},
			loadChatList(chatUserId) {
			      this.form = {}
			      this.activeChatUserId = chatUserId
			
			      // 更新消息读取状态
			      this.$request.put('/chatInfo/updateRead/' + chatUserId).then(res => {
			
			        this.loadChatGroup()
			      })
			      this.$request.get('/chatInfo/selectUserChat/' + chatUserId).then(res => {
			        this.chatList = res.data || []
			       
			      })
			    },
			focus(){
				this.scrollToBottom()
			},
			blur(){
				this.scrollToBottom()
			},
			// px转换成rpx
			rpxTopx(px){
				let deviceWidth = uni.getSystemInfoSync().windowWidth
				let rpx = ( 750 / deviceWidth ) * Number(px)
				return Math.floor(rpx)
			},
			// 监视聊天发送栏高度
			sendHeight(){
				setTimeout(()=>{
					let query = uni.createSelectorQuery();
					query.select('.send-msg').boundingClientRect()
					query.exec(res =>{
						this.bottomHeight = this.rpxTopx(res[0].height)
					})
				},10)
			},
			// 滚动至聊天底部
			scrollToBottom(e){
				setTimeout(()=>{
					let query = uni.createSelectorQuery().in(this);
					query.select('#scrollview').boundingClientRect();
					query.select('#msglistview').boundingClientRect();
					query.exec((res) =>{
						if(res[1].height > res[0].height){
							this.scrollTop = this.rpxTopx(res[1].height - res[0].height)
						}
					})
				},15)
			},
			// 发送消息
			handleSend() {
	
				      this.form.chatUserId = this.activeChatUserId
				      this.form.userId = this.user.id
					  this.form.text =this.chatMsg
				      this.$request.post('/chatInfo/add', this.form).then(res => {
				        this.form = {}
				        this.loadChatList(this.activeChatUserId)
						
				        // 发送socket消息
						uni.sendSocketMessage({
						  data: this.chatMsg,
						  success() {
						    console.log('消息发送成功');
						  },
						  fail(err) {
						    console.error('消息发送失败', err);
						  }
						});

				      })
				//如果消息不为空
				if(!this.chatMsg||!/^\s+$/.test(this.chatMsg)){
					let obj = {
						botContent: "",
						userContent: this.chatMsg,
            image:"/static/common/unname2.jpg"
					}
					this.msgList.push(obj);
					this.chatMsg = '';
					this.scrollToBottom()
				}else {
					this.$modal.showToast('不能发送空白消息')
				}
			},
		}
	}
</script>
<style lang="scss" scoped>
	
	$chatContentbgc: #C2DCFF;
	$sendBtnbgc: #4F7DF5;
	
	view,button,text,input,textarea {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}
 
	/* 聊天消息 */
	.chat {
     .topTabbar {
          width: 100%;
          height: 90rpx;
          line-height: 90rpx;
          display: flex;
          margin-top: 80rpx;
          justify-content: space-between;
      
          .icon {
            margin-left: 20rpx;
          }
      
          .text {
            margin: auto;
            font-size: 16px;
            font-weight: 700;
          }
      
          .button {
            width: 10%;
            margin: auto 20rpx auto 0rpx;
          }
        }
		.scroll-view {
			::-webkit-scrollbar {
					    display: none;
					    width: 0 !important;
					    height: 0 !important;
					    -webkit-appearance: none;
					    background: transparent;
					    color: transparent;
					  }
			
			// background-color: orange;
			background-color: #F6F6F6;
			
			.chat-body {
				display: flex;
				flex-direction: column;
				padding-top: 23rpx;
				// background-color:skyblue;
				
				.self {
					justify-content: flex-end;
				}
				.item {
					display: flex;
					padding: 23rpx 30rpx;
					// background-color: greenyellow;
 
					.right {
						background-color: $chatContentbgc;
					}
					.left {
						background-color: #FFFFFF;
					}
                    // 聊天消息的三角形
					.right::after {
						position: absolute;
						display: inline-block;
						content: '';
						width: 0;
						height: 0;
						left: 100%;
						top: 10px;
						border: 12rpx solid transparent;
						border-left: 12rpx solid $chatContentbgc;
					}
 
					.left::after {
						position: absolute;
						display: inline-block;
						content: '';
						width: 0;
						height: 0;
						top: 10px;
						right: 100%;
						border: 12rpx solid transparent;
						border-right: 12rpx solid #FFFFFF;
					}
 
					.content {
						position: relative;
						max-width: 486rpx;
						border-radius: 8rpx;
						word-wrap: break-word;
						padding: 24rpx 24rpx;
						margin: 0 24rpx;
						border-radius: 5px;
						font-size: 32rpx;
						font-family: PingFang SC;
						font-weight: 500;
						color: #333333;
						line-height: 42rpx;
					}
 
					.avatar {
						display: flex;
						justify-content: center;
						width: 78rpx;
						height: 78rpx;
						background: $sendBtnbgc;
						border-radius: 50rpx;
						overflow: hidden;
						
						image {
							align-self: center;
						}
 
					}
				}
			}
		}
 
		/* 底部聊天发送栏 */
		.chat-bottom {
			width: 100%;
			height: 100rpx;
			background: #F4F5F7;
			transition: all 0.1s ease;
			
			.send-msg {
				display: flex;
				align-items: flex-end;
				padding: 16rpx 30rpx;
				width: 100%;
				min-height: 177rpx;
				position: fixed;
				bottom: 0;
				background: #fff;
				transition: all 0.1s ease;
			}
 
			.uni-textarea {
				padding-bottom: 70rpx;  
				textarea {
					width: 537rpx;
					min-height: 75rpx;
					max-height: 500rpx;
					background: #f1f1f1;
					border-radius: 40rpx;
					font-size: 32rpx;
					font-family: PingFang SC;
					color: #333333;
					line-height: 74rpx;
					padding: 5rpx 8rpx;
          text-indent: 30rpx;
				}
			}
            
			.send-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 76rpx;
				margin-left: 25rpx;
				width: 120rpx;
				height: 75rpx;
				background: #ed5a65;
				border-radius: 50rpx;
				font-size: 28rpx;
				font-family: PingFang SC;
				font-weight: 500;
				color: #FFFFFF;
				line-height: 28rpx;
			}
		}
	}
</style>