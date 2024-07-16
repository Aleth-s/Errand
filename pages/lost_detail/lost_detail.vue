<template>
	<view style="padding: 20rpx;">
		<view class="box">
			<view class="box">
				<view class="line" style="margin-bottom: 40rpx;">
					<view class="line-title">物品名称：</view>
					<view class="line-content">{{ orders.title}}</view>
				</view>

				<view class="line">
					<view class="line-title">物品描述：</view>
					<view class="line-content">{{ orders.content}}</view>
				</view>
				<view class="line">
					<view class="line-title">物品价格：</view>
					<view class="line-content">{{ orders.price }}</view>
				</view>
				<view class="line">
					<view class="line-title">卖家用户名：</view>
					<view class="line-content">{{ list1.username }}</view>
				</view>

				<view class="line">
					<view class="line-title">买家用户名：</view>
					<view class="line-content">{{ list2.username}}</view>
				</view>
				<view class="line">
					<view class="line-title">价格:</view>
					<view class="line-content">{{ orders.price}}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
					<view class="line-title">重量：</view>
					<view class="line-content">{{ orders.otherinfo }}</view>
			</view>

			<view class="line">
				<view class="line-title" style="color: blue;">购买理由：</view>
				<view class="line-content">{{ orders.talk }}</view>
			</view>

		
			</view>
			
			
	


			
			<view><button type="primary" class="my-button-primary" @click="back">确认</button></view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				orders: {},
				showInfo: false,
				list1:{},
				list2:{},
				coo:false,
				wordCloudImageUrl: null,
				id:0, 
				title: 'map',
				latitude: 39.909,
				longitude: 116.39742,
				markers: [
				        {
				          iconPath: "/static/imgs/骑手32.png", // 骑手的标记图片
				          id: 1,
				          latitude: 39.909,
				          longitude: 116.39742,
				          width: 30,
				          height: 30,
						  callout: {
						          content: '骑手位置',
						          color: '#ffffff',
						          fontSize: 14,
						          borderRadius: 10,
						          bgColor: '#000000',
						          padding: 10,
						          display: 'ALWAYS',
						        },
				        },]
				     
			}
		},
		onLoad(option) {
			const sId = option.sId
			this.load(sId)
		/* 	this.getRiderLocation(); // 页面加载时获取骑手位置
			    setInterval(() => {
			      this.getRiderLocation(orderId); // 每隔一段时间获取骑手位置
			    }, 5000); // 5秒更新一次位置，可以根据实际需求调整 */
		},
		methods: {
			back() {
				uni.navigateBack()
			},
			load(sId) { 
				this.$request.get('/transaction/selectById/' + sId).then(res => {
					this.orders = res.data || {}
					this.list1=res.data.post_user || {}
					this.list2 = res.data.accept_user || {};
										
				})
			},
			toggleInfo() {
			uni.navigateTo({
				url: '/pages/riderInfo/riderInfo?orderId=' + this.orders.id
			})
			},
			getRiderLocation(orderId) {
			      // 通过接口获取骑手位置信息
			      // 示例接口：/rider/getLocation
			      wx.request({
			        url: "/rider/getLocation/"+ this.orders.id,
			        success: (res) => {
			          const { latitude, longitude } = res.data;
			          this.riderLocation = { latitude, longitude };
			          this.markers[0].latitude = latitude;
			          this.markers[0].longitude = longitude;
			        },
			        fail: (error) => {
			          console.error("Failed to get rider location:", error);
			        },
			      });}
			
		}
	}
</script>

<style>
	.line {
		display: flex;
		margin-bottom: 10rpx;
		grid-gap: 10rpx;
	}

	.line-title {
		width: 200rpx;
		font-weight: bold;
		text-align: right;
	}

	.line-content {
		flex: 1;
	}

</style>