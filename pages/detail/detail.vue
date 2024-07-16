<template>
	<view style="padding: 20rpx;">
		<view class="box">
			<view class="box">
				<view class="line" style="margin-bottom: 40rpx;">
					<view class="line-title">订单编号：</view>
					<view class="line-content">{{ orders.orderNo }}</view>
				</view>

				<view class="line">
					<view class="line-title">取货地址：</view>
					<view class="line-content">{{ orders.address.address + orders.address.doorNo }}</view>
				</view>
				<view class="line">
					<view class="line-title">取货联系人：</view>
					<view class="line-content">{{ orders.address.userName }}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
					<view class="line-title">取货电话：</view>
					<view class="line-content">{{ orders.address.phone }}</view>
				</view>

				<view class="line">
					<view class="line-title">收货地址：</view>
					<view class="line-content">{{ orders.targetAddress.address + orders.targetAddress.doorNo }}</view>
				</view>
				<view class="line">
					<view class="line-title">收货联系人：</view>
					<view class="line-content">{{ orders.targetAddress.userName }}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
					<view class="line-title">收货电话：</view>
					<view class="line-content">{{ orders.targetAddress.phone }}</view>
			</view>

			<view class="line">
				<view class="line-title">物品名称：</view>
				<view class="line-content">{{ orders.name }}</view>
			</view>
			<view class="line">
				<view class="line-title">物品描述：</view>
				<view class="line-content">{{ orders.descr }}</view>
			</view>
			<view class="line">
				<view class="line-title">物品图片：</view>
				<view class="line-content">
					<image :src="orders.img" mode="widthFix" style="width: 180rpx;"></image>
				</view>
			</view>
			<view class="line">
				<view class="line-title">物品类型：</view>
				<view class="line-content">{{ orders.type }}</view>
			</view>
			<view class="line">
				<view class="line-title">物品重量：</view>
				<view class="line-content">{{ orders.weight }} kg</view>
			</view>
			<view class="line">
				<view class="line-title">小费：</view>
				<view class="line-content" style="color: red;">￥{{ orders.weight }}</view>
			</view>
			<view class="line" style="margin-bottom: 40rpx;">
				<view class="line-title">下单时间：</view>
				<view class="line-content">{{ orders.time }}</view>
			</view>
			<view class="line" style="margin-bottom: 40rpx;">
				<view class="line-title">备注：</view>
				<view class="line-content">{{ orders.comment }}</view>
			</view>
			<view class="line" style="margin-bottom: 40rpx;">
				<view class="line-title">接单状态：</view>
				<view class="line-content" style="color:blue;" v-if="coo">骑手已接单</view>
				<view class="line-content" style="color:blue;" v-if="!coo">骑手未接单</view>
			</view>
			</view>
			
			
			
			<view class="box" v-if="coo" >
				
				<view class="line" style="margin-bottom: 40rpx;">
				    <view class="line-title line-title-blue">骑手姓名:</view>
				    <view class="line-content">{{ orders.certification.name }}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
				    <view class="line-title line-title-blue">骑手电话:</view>
				    <view class="line-content">{{ orders.certification.phone }}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
				            <view class="line-title line-title-blue" style="padding: 7px 0;">平均评分:</view>
							<uni-rate :value="orders.averageStar" readonly></uni-rate>
				        </view>
			    <view style="padding: 0 35px;margin-bottom: 40rpx;">           
				<uni-tag text="骑手信息详情" type="primary" size="large;" @tap.native.stop="toggleInfo" ></uni-tag>
			    </view>
					<view>
						<view class="page-body">
							<view class="page-section page-section-gap">
								<map style="width: 100%; height: 200px;" :latitude="latitude" :longitude="longitude" :markers="markers">
								</map>
							</view>
						</view>
					</view>
				
			</view>


			
			<view><button type="primary" class="my-button-primary" @click="back">确认</button></view>
		</view>
	</view>
</template>

<script>
	import amap from '../../common/amap-wx.130.js';  
	export default {
		data() {
			return {
				forms:{},
				user: uni.getStorageSync('xm-user'),
				orders: {},
				showInfo: false,
				colist:[],
				cerlist:{},
				coo:false,
				wordCloudImageUrl: null,
				id:0, 
				title: 'map',
				latitude: 0,
				longitude: 0,
				markers: [
				        {
				          iconPath: "/static/imgs/qishou.png", // 骑手的标记图片
				          id: 1,
				          latitude: 0,
				          longitude: 0,
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
				        },],
				amapPlugin: null,
				key: 'efe0a71f0e57210545d9f90650ea1397',  
				addressName: '',  
				weather: {  
				hasData: false,  
				data: []  }
						
				     
			}
		},
		onLoad(option) {
			this.amapPlugin = new amap.AMapWX({
			            key: this.key  
			        });  
			const orderId = option.orderId
			this.load(orderId)
			
		 	this.getRegeo(orderId); // 页面加载时获取骑手位置
			    setInterval(() => {
			      this.getRegeo(orderId); // 每隔一段时间获取骑手位置
			    }, 15000); // 5秒更新一次位置，可以根据实际需求调整 */
		
		
		},
		methods: {
			getRegeo(orderId) {
			        // uni.showLoading({  
			        //     title: '获取信息中11'  
			        // });
			
			        this.amapPlugin.getRegeo({  
			            success: (data) => {  
			                console.log(data)
							console.log(data[0].longitude)
							console.log(data[0].latitude)
							if(this.user.role=="rider"){
								this.$request.put('/orders/update', {longitude: data[0].longitude, // 假设帖子有唯一的 id 属性
								  latitude: data[0].latitude,id:orderId
								}).then(() => {})
							}
							this.$request.get('/orders/selectById/'+ orderId).then(res => {
								this.longitude=res.data.longitude
								this.latitude=res.data.latitude
								this.markers[0].longitude=res.data.longitude
								this.markers[0].latitude=res.data.latitude
							})
							// this.longitude=data[0].longitude
							// this.latitude=data[0].latitude
							// this.markers[0].longitude=data[0].longitude
							// this.markers[0].latitude=data[0].latitude
							
			                this.addressName = data[0].name;  
			                uni.hideLoading();  
							
			            }  ,
						fail(e){
						
						console.log(e)
						
						}
			        });  
			    } ,
			back() {
				uni.navigateBack()
			},
			load(orderId) { 
				this.$request.get('/orders/selectById/' + orderId).then(res => {
					this.orders = res.data || {}
					this.colist=res.data.commentc || []
					 this.cerlist = res.data.certification || {};
					
					            // 添加条件判断
					            if (Object.keys(this.cerlist).length > 0) {
					                this.coo = true;  // 如果cerlist不为空，显示信息
					            }
								if (this.colist.length > 0) {
								                let totalStar = 0;
								                this.colist.forEach(comment => {
								                    totalStar += comment.star;
								                });
								                const averageStar = totalStar / this.colist.length;
								
								                // 存储平均star到orders中
								                this.orders.averageStar = averageStar;
								        }
										
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
