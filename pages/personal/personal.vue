<template>
	<view style="padding: 20rpx;">
		 <view style="position: fixed; right: 20rpx; top: 20rpx;">  
		      <image @click="goPage('/pages/chat/chat')" src="../../static/kefu.png" style="width: 75rpx; height: 75rpx;"></image> 
			   <view style="position: fixed; right: 35rpx;">客服</view>
		    </view>  
		<view style="text-align: center;">
			<image :src="user.avatar" style="width: 200rpx; height: 200rpx; border-radius: 50%;"></image>
			<view style="margin: 10rpx 0;">{{ user.name }}</view>
			<view v-if="user.rider">
				<uni-icons color="#006eff" type="vip-filled" size="18" style="position: relative; top:2rpx;"></uni-icons>
				<view style="color: #006eff;"><uni-forms type="vip-filled"></uni-forms>认证骑手</view>
			</view>
		</view>
		
		
		
		<view style="margin: 20rpx 0; " class="box">
			<view class="title">用户服务</view>
			<view style="display: flex;">
				<view class="cartegory-item" @click="goPage('/pages/address/address')">
					<image src="../../static/imgs/dizhi.png" style="width: 50%;" mode="widthFix"></image>
					<view style="flex: 1;">我的地址</view>
				</view>
				<view class="cartegory-item" @click="goPage('/pages/records/records')">
					<image src="../../static/imgs/shouzhi.png" style="width: 50%;" mode="widthFix"></image>
					<view style="flex: 1;">收支明细</view>
				</view>
				<view class="cartegory-item" @click="goPage('/pages/commentct/commentct')">
					<image src="../../static/imgs/pingjia.png" style="width: 50%;" mode="widthFix"></image>
					<view style="flex: 1;">评价中心</view>
				</view>
				<view class="cartegory-item" @click="goPage('/pages/wallet/wallet')">
					<image src="../../static/imgs/qianbao.png" style="width: 50%;" mode="widthFix"></image>
					<view style="flex: 1;">我的钱包</view>
				</view>
			</view>
		</view>
		
		<view style="margin: 20rpx 0; " class="box">
			<view class="title">骑手服务</view>
			<view style="display: flex;">
				<view class="cartegory-item" @click="goPage('/pages/certification/certification')">
					<image src="../../static/imgs/renzheng.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">骑手认证</view>
				</view>
				<view class="cartegory-item" @click="goPage2('/pages/acceptOrders/acceptOrders')">
					<image src="../../static/imgs/paotui.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">骑手订单</view>
				</view>
				<view class="cartegory-item" @click="goPage2('/pages/ridercomment/ridercomment')">
					<image src="../../static/imgs/chakan.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">骑手评价</view>
				</view>
				<view class="cartegory-item" @click="goPage2('/pages/analysis/analysis')">
					<image src="../../static/transa.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">骑手分析</view>
				</view>
			</view>
		</view>
		
		<view style="margin: 20rpx 0; " class="box">
			<view class="title">校园服务</view>
			<view style="display: flex;">
				<view class="cartegory-item" @click="goPage('/pages/forum/forum')">
					<image src="../../static/forum-.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">校园墙</view>
				</view>
				<view class="cartegory-item" @click="goPage('/pages/transaction/transaction')">
					<image src="../../static/transa.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">二手交易</view>
				</view>
				<view class="cartegory-item" @click="goPage('/pages/lost/lost')">
					<image src="../../static/lost.png" style="width: 30%;" mode="widthFix"></image>
					<view style="flex: 1;">失物招领</view>
				</view>
		
			</view>
		</view>
		
		<view style="margin: 20rpx 0;" class="box">
			<view class="info-item" @click="goPage('/pages/personalinformation/personalinformation')">
				<uni-icons type="person" size="18"></uni-icons>
				<text style="margin-left: 10rpx;">个人信息</text>
			</view>
			<view class="info-item" @click="goPage('/pages/introduce/introduce')">
				<uni-icons type="medal" size="18"></uni-icons>
				<text style="margin-left: 10rpx;">平台介绍</text>
			</view>
			<view class="info-item" @click="goPage('/pages/agreement/agreement')">
				<uni-icons type="paperclip" size="18"></uni-icons>
				<text style="margin-left: 10rpx;">用户协议</text>
			</view>
			<view class="info-item" @click="logout">
				<uni-icons type="undo" size="18"></uni-icons>
				<text style="margin-left: 10rpx;">退出登录</text>
			</view>
			
		</view>
		
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				user: uni.getStorageSync('xm-user')
			}
		},
		methods: {
			logout() {
				uni.removeStorageSync('xm-user')
				uni.removeStorageSync('orderStore')  // 清除所有缓存数据
				uni.reLaunch({
					url: '/pages/login/login'
				})
			},
			goPage(url) {
			
				uni.navigateTo({
					url: url
				})
			},
			goPage2(url) {
				if (!this.user.rider) {  // 判断是否是骑手
					uni.showToast({
						icon: 'none',
						title: '只有认证骑手才可以查看'
					})
					return
				}
				uni.navigateTo({
					url: url
				})
			}
			

	},
	}
</script>

<style>
.title {
	font-weight: bold;
	font-size: 36rpx;
	margin-bottom: 30rpx;
}
.cartegory-item {
	flex: 1; 
	display: flex; 
	justify-content: space-between; 
	align-items: center; 
	flex-direction: column; 
	grid-gap: 10rpx;
}
.info-item {
	padding: 15rpx; 
	border-bottom: 2rpx solid #eee;
}
.info-item .uni-icons {
	position: relative;
	top: 2rpx;
}
</style>
