<template>
	<view style="padding: 20rpx;">
				
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
				<view class="line" style="margin-bottom: 40rpx;">
				    <view class="line-title line-title-blue">通讯地址:</view>
				    <view class="line-content">{{ orders.certification.address }}</view>
				</view>
				<view class="line" style="margin-bottom: 40rpx;">
				    <view class="line-title line-title-blue">骑手照片:</view>
				    <view class="line-content">
				        <image :src="orders.certification.avatar" mode="widthFix" style="width: 180rpx;" @tap.native.stop="viewRiderPhoto"></image>
				    </view>
				</view>
				<!-- <view class="line" style="margin-bottom: 40rpx;">
				       
					<view class="line-title line-title-blue">评价词云:</view>
				    <view class="line-content">    
						<image @click.native.stop="previewWordcloud" :src="wordcloudImageUrl" mode="widthFix" style="width: 300rpx;"></image>
					</view>
				</view> -->
				<view class="line-title line-title-blue">评价词云:</view>
				
				<view style="margin-top: 20rpx;">
					<image  :src="wordcloudImageUrl"  style="width: 670rpx;"></image>
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
				orders: {},
				showInfo: false,
				colist:[],
				cerlist:{},
				coo:false,
				orderId: 0,
				wordcloudImageUrl: '',
				word:'',
				amapPlugin: null,  
				key: 'efe0a71f0e57210545d9f90650ea1397',  
				addressName: '',  
				weather: {  
				hasData: false,  
				data: []  
				} 
			}
		},
		onLoad(option) {
			this.amapPlugin = new amap.AMapWX({  
			            key: this.key  
			        });  
			const orderId = option.orderId
			this.load(orderId)
		},
		methods: {
			    getRegeo() {  
			            uni.showLoading({  
			                title: '获取信息中11'  
			            });
				
			            this.amapPlugin.getRegeo({  
			                success: (data) => {  
			                    console.log(data)
								console.log(data[0].longitude)
								console.log(data[0].latitude)
								
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
			viewRiderPhoto() {
			        // 处理查看骑手照片的逻辑
			        // 可以使用 uni.previewImage 或导航到新页面显示更大的图片
			        // 例如，你可以使用 uni.previewImage 或导航到新页面显示更大的图片
			        uni.previewImage({
			            current: this.orders.certification.avatar,
			            urls: [this.orders.certification.avatar]
			        });
					},
					
			 previewWordcloud() {
			    uni.previewImage({
			      urls: [this.wordcloudImageUrl], // 需要预览的图片链接列表
			      current: this.wordcloudImageUrl, // 当前显示图片的链接
			    success: () => {
			                    // 设置预览图片样式，例如取消变暗效果
			                    uni.createSelectorQuery().select('.uni-image-preview-content').boundingClientRect(data => {
			                        if (data) {
			                            const previewContent = data;
			                            // 检查样式是否存在并进行适当的设置
			                            if (previewContent && previewContent.style) {
			                                previewContent.style.filter = 'brightness(100%)'; // 设置亮度，取消变暗效果
			                            }
			                        }
			                    }).exec();}
				})
				
				},
				
			load(orderId) { 
			    this.$request.get('/orders/selectById/' + orderId)
			      .then(res => {
			        this.orders = res.data || {};
			        this.colist = res.data.commentc || [];
			        this.cerlist = res.data.certification || {};
			
			        // 添加条件判断
			        if (Object.keys(this.cerlist).length > 0) {
			          this.coo = true;  // 如果 cerlist 不为空，显示信息
			        }
			
			        if (this.colist.length > 0) {
			          let totalStar = 0;
			          this.colist.forEach(comment => {
			            totalStar += comment.star;
			          });
			          const averageStar = totalStar / this.colist.length;
			
			          // 存储平均 star 到 orders 中
			          this.orders.averageStar = averageStar;
			        }
			
			    
			        // 加载词云图
			        this.$request.get('/orders/wordcloud/' + orderId)
			          .then(res => {
					
						
			            this.wordcloudImageUrl = 'data:image/png;base64,' + res.data.wordcloudImage;
		
			          })
			          .catch((error) => {
			            console.error('Failed to load word cloud image:', error);
			          });
			      })
			      .catch(error => {
			        console.error('Failed to load order:', error);
			      });
				  this.getRegeo()
			  }
			},
			
			
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