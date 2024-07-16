<template>
	<view style="padding: 20rpx;">
		<view style="margin-bottom: 20rpx;">
			<swiper circular autoplay :interval="3000" :duration="500" indicator-dots style="height: 320rpx;" 
			  indicator-color="rgba(255, 255, 255, 0.6)" indicator-active-color="#006eff">
			  <swiper-item v-for="item in imgs" :key="item" >
			    <image :src="item" alt="" style="width: 100%; height: 350rpx;" />
			  </swiper-item>
			</swiper>
		</view>
		
		<view style="margin-bottom: 20rpx;">
			<uni-notice-bar v-if="content" show-icon single :text="content" />
		</view>
		
		<view style="display: flex; margin-bottom: 20rpx;" class="box">
			<view class="cartegory-item" @click="goPreOrder('代拿快递')">
				<image src="../../static/imgs/tuangou.png" style="width: 50%;" mode="widthFix"></image>
				<view style="flex: ;">代拿快递</view>
			</view>
			<view class="cartegory-item" @click="goPreOrder('代取餐品')">
				<image src="../../static/imgs/shutiao.png" style="width: 50%;" mode="widthFix"></image>
				<view style="flex: 1;">代取餐品</view>
			</view>
			<view class="cartegory-item" @click="goPreOrder('代送物品')">
				<image src="../../static/imgs/wuping.png" style="width: 50%;" mode="widthFix"></image>
				<view style="flex: 1;">代送物品</view>
			</view>
			<view class="cartegory-item" @click="goPreOrder('其他代办')">
				<image src="../../static/imgs/qita.png" style="width: 50%;" mode="widthFix"></image>
				<view style="flex: 1;">其他代办</view>
			</view>
			
		</view>
		
		<!-- 骑手 -->
		<view v-if="user.rider">
			<view class="box" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;">校园已发布订单</view>
			<view >
				<view v-for="item in orderList" :key="item.id" class="box" style="margin-bottom: 10rpx;" @click="goDetail(item.id)">
					<view style="display: flex; align-items: center; margin-bottom: 20rpx;">
						<view style="flex: 1;">
							<uni-tag text="餐品" size="small" type="success" v-if="item.type === '代取餐品'"></uni-tag>
							<uni-tag text="快递" size="small" type="primary" v-if="item.type === '代拿快递'"></uni-tag> 
							<uni-tag text="物品" size="small" type="warning" v-if="item.type === '代送物品'"></uni-tag> 
							<uni-tag text="其他" size="small" type="error" v-if="item.type === '其他代办'"></uni-tag> 
							<uni-tag text="二手交易" size="small" type="default" v-if="item.type === '二手交易'"></uni-tag> 
							<text style="margin-left: 10rpx;">{{ item.name }}</text>
						</view>
						<view style="flex: 1; text-align: right;">
							<text style="color: #888;">跑腿费</text>
							<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
						</view>
					</view>
					
					<view style="display: flex; align-items: center;" v-if="item.userId!==user.id">
						<view style="flex: 1;">
							<text style="margin-right: 10rpx;">已下单{{ item.range }}分钟</text>
							<text style="color: orange;">待接单</text>
						</view>
						<view style="flex: 1; text-align: right;"  v-if="user.rider">
							<uni-tag text="接单" type="primary" size="small" @click.native.stop="accept(item)" ></uni-tag>
						</view>
					</view>
					<view style="display: flex; align-items: center;" v-if="item.userId===user.id">
						<view style="flex: 1;">
							<text style="margin-right: 10rpx;">已下单{{ item.range }}分钟</text>
							<text style="color: blue;">本人订单</text>
						</view>
						
					</view>
				</view>
			</view>
			<view class="box" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;margin-top: 13px;">已接订单</view>
			<view>
				<view v-for="item in orderList2" :key="item.id" class="box" style="margin-bottom: 10rpx;"
					@click="goDetail(item.id)" v-if="item.status==='待送达'">
					<view style="display: flex; align-items: center; margin-bottom: 20rpx;">
						<view style="flex: 1;">
							<uni-tag text="餐品" size="small" type="success" v-if="item.type === '代取餐品'"></uni-tag>
							<uni-tag text="快递" size="small" type="primary" v-if="item.type === '代拿快递'"></uni-tag>
							<uni-tag text="物品" size="small" type="warning" v-if="item.type === '代送物品'"></uni-tag>
							<uni-tag text="其他" size="small" type="error" v-if="item.type === '其他代办'"></uni-tag>
							<uni-tag text="二手交易" size="small" type="default" v-if="item.type === '二手交易'"></uni-tag> 
							<text style="margin-left: 10rpx;">{{ item.name }}</text>
						</view>
						<view style="flex: 1; text-align: right;">
							<text style="color: #888;">跑腿费</text>
							<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
						</view>
					</view>
			
					<view style="display: flex; align-items: center;">
						<view style="flex: 1;">
							<text style="margin-right: 20rpx;"
								v-if="item.status === '待接单' || item.status === '待送达' || item.status === '待收货'">已下单{{ item.range }}分钟</text>
			
							<text style="color: #888;" v-if="item.status === '已取消'">{{ item.status }}</text>
							<text style="color: orange;" v-if="item.status === '待接单'">{{ item.status }}</text>
							<text style="color: dodgerblue" v-if="item.status === '待送达'">{{ item.status }}</text>
							<text style="color: mediumpurple;" v-if="item.status === '待收货'">{{ item.status }}</text>
							<text style="color: indianred;" v-if="item.status === '待评价'">{{ item.status }}</text>
							<text style="color: #18bc37;" v-if="item.status === '已完成'">{{ item.status }}</text>
						</view>
						<view style="flex: 1; text-align: right;">
							<uni-tag v-if="item.status === '待送达'" text="确认送达" type="primary" size="small"
								@click.native.stop="arrive(item)"></uni-tag>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		
		<!-- 普通用户 -->
		<view v-if="!user.rider">
			<view class="box" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;">代办订单</view>
			<view >
				<view v-for="item in orderList1" :key="item.id" class="box" style="margin-bottom: 10rpx;" @click="goDetail(item.id)" v-if="item.userId === user.id" >
					<view style="display: flex; align-items: center; margin-bottom: 20rpx;" >
						<view style="flex: 1;">
							<uni-tag text="餐品" size="small" type="success" v-if="item.type === '代取餐品'"></uni-tag>
							<uni-tag text="快递" size="small" type="primary" v-if="item.type === '代拿快递'"></uni-tag> 
							<uni-tag text="物品" size="small" type="warning" v-if="item.type === '代送物品'"></uni-tag> 
							<uni-tag text="其他" size="small" type="error" v-if="item.type === '其他代办'"></uni-tag> 
							<uni-tag text="二手交易" size="small" type="default" v-if="item.type === '二手交易'"></uni-tag> 
							<text style="margin-left: 10rpx;">{{ item.name }}</text>
						</view>
						<view style="flex: 1; text-align: right;">
							<text style="color: #888;">跑腿费</text>
							<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
						</view>
					</view>
					<view style="display: flex; align-items: center;">
						<view style="flex: 1;">
							<text style="margin-right: 20rpx;"
								v-if="item.status === '待接单' || item.status === '待送达' || item.status === '待收货'">已下单{{ item.range }}分钟</text>
								
							<text style="color: #888;" v-if="item.status === '已取消'">{{ item.status }}</text>
							<text style="color: orange;" v-if="item.status === '待接单'">{{ item.status }}</text>
							<text style="color: dodgerblue" v-if="item.status === '待送达'">{{ item.status }}</text>
							<text style="color: mediumpurple;" v-if="item.status === '待收货'">{{ item.status }}</text>
							<text style="color: indianred;" v-if="item.status === '待评价'">{{ item.status }}</text>
							<text style="color: #18bc37;" v-if="item.status === '已完成'">{{ item.status }}</text>
						</view>
						
						<view style="display: inline-block;">
							<uni-icons type="trash" size="18" color="#888"
								style="position: relative; top: 4rpx;"></uni-icons>
							<text style="color: #888;" @click.native.stop="handleDel(item.id)">删除</text>
						</view>
						
						<view style="flex: 1; text-align: right;">
						<uni-tag text="确认收货" type="primary" size="small" v-if="item.status === '待收货'" @click.native.stop="changeStatus(item,'待评价')"></uni-tag>
						</view>
					</view>
					</view >
					
				</view>
				
				<view v-for="item in orderList" :key="item.id" class="box" style="margin-bottom: 10rpx;" @click="goDetail(item.id)" v-if="item.userId === user.id" >
					<view style="display: flex; align-items: center; margin-bottom: 20rpx;" >
						<view style="flex: 1;">
							<uni-tag text="餐品" size="small" type="success" v-if="item.type === '代取餐品'"></uni-tag>
							<uni-tag text="快递" size="small" type="primary" v-if="item.type === '代拿快递'"></uni-tag> 
							<uni-tag text="物品" size="small" type="warning" v-if="item.type === '代送物品'"></uni-tag> 
							<uni-tag text="其他" size="small" type="error" v-if="item.type === '其他代办'"></uni-tag> 
							<uni-tag text="二手交易" size="small" type="default" v-if="item.type === '二手交易'"></uni-tag> 
							<text style="margin-left: 10rpx;">{{ item.name }}</text>
						</view>
						<view style="flex: 1; text-align: right;">
							<text style="color: #888;">跑腿费</text>
							<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
						</view>
					</view>
					<view style="flex: 1;">
						<text style="margin-right: 10rpx;">已下单{{ item.range }}分钟</text>
						<text style="color: blue;">本人订单</text>
					</view>
					
			    </view >
				<view v-if="!user.rider">	
				</view>
				<view class="box" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;margin-top: 13px;">二手交易订单</view>
				<view>
					<!-- <view v-for="item in orderList1" :key="item.id" class="box" style="margin-bottom: 10rpx;" @click="goDetail(item.id)" v-if="item.userId === user.id" >
						<view style="display: flex; align-items: center; margin-bottom: 20rpx;" >
							<view style="flex: 1;">
								<uni-tag text="二手" size="small" type="success" v-if="item.type === '二手'"></uni-tag>
								
								<text style="margin-left: 10rpx;">{{ item.name }}</text>
							</view>
							<view style="flex: 1; text-align: right;">
								<text style="color: #888;">跑腿费</text>
								<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
							</view>
						</view>
						<view style="display: flex; align-items: center;">
							<view style="flex: 1;">
								<text style="margin-right: 20rpx;"
									v-if="item.status === '待接单' || item.status === '待送达' || item.status === '待收货'">已下单{{ item.range }}分钟</text>
									
								<text style="color: #888;" v-if="item.status === '已取消'">{{ item.status }}</text>
								<text style="color: orange;" v-if="item.status === '待接单'">{{ item.status }}</text>
								<text style="color: dodgerblue" v-if="item.status === '待送达'">{{ item.status }}</text>
								<text style="color: mediumpurple;" v-if="item.status === '待收货'">{{ item.status }}</text>
								<text style="color: indianred;" v-if="item.status === '待评价'">{{ item.status }}</text>
								<text style="color: #18bc37;" v-if="item.status === '已完成'">{{ item.status }}</text>
							</view>
							<view style="flex: 1; text-align: right;">
							<uni-tag text="确认收货" type="primary" size="small" v-if="item.status === '待收货'" @click.native.stop="changeStatus(item,'待评价')"></uni-tag>
							</view>
						</view>
						</view >
						
					</view> -->
					
					<view v-for="item in transaction" :key="item.id" class="box" style="margin-bottom: 10rpx;" @click="goSDetail(item.id)" v-if="item.acceptId && item.userId==user.id" >
						<view style="display: flex; align-items: center; margin-bottom: 20rpx;" >
							<view style="flex: 1;">
								<uni-tag text="二手交易" size="small" type="success" ></uni-tag>

								<text style="margin-left: 10rpx;">{{ item.title }}</text>
							</view>
							<view style="flex: 1; text-align: right;">
								<text style="color: #888;">跑腿费</text>
								<text style="color: red; font-size: 34rpx;">￥{{ item.price }}</text>
							</view>
							
						</view>
						<view style="display: flex; align-items: center;margin-bottom: 10rpx;">
							<view style="flex: 1;">
											
								<text style="color: blue;">买家已下单</text>
							</view>
							
							<view style="flex: 1; text-align: right;">
							<uni-tag text="确认交易" type="primary" size="small"  @click.native.stop="addorder(item)"></uni-tag>
							<uni-icons type="trash" size="18" color="#888"
								style="position: relative; top: 4rpx;"></uni-icons>
							<text style="color: #888;" @click.native.stop="del2(item.id)">取消交易</text>
							</view>
						</view>
						
						
				    </view >
						<view class="box" style="color: #006eff; font-weight: bold; margin-bottom: 10rpx;margin-top: 13px;">失物招领订单</view>
						
					</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				imgs: [
				  require('../../static/imgs/banner1.png'),
				  require('../../static/imgs/banner2.png'),
				],
				content: '',
				noticeList: [],
				inter: null,
				orderList: [],
				user: uni.getStorageSync('xm-user'),
				orderList2:[],
				orderList1:[],
				transaction:[],
				form: {},
			}
		},
		onShow() {
			this.load()
			this.loadNotice()
		},
		onHide() {
			clearInterval(this.inter)
			this.inter = null
		},
		methods: {
			del2(id){
				this.$request.del('/transaction/delete/' + id).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			},
			
			accept(orders) {
				if (!this.user.rider) {  // 判断是否是骑手
					uni.showToast({
						icon: 'none',
						title: '只有认证骑手才可以接单'
					})
					return
				}
				this.$request.put('/orders/accept', orders).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load() 
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			},
			goDetail(orderId) {
				uni.navigateTo({
					url: '/pages/detail/detail?orderId=' + orderId
				})
			},
			goSDetail(sId) {
				uni.navigateTo({
					url: '/pages/trans_detail/trans_detail?sId=' + sId
				})
			},
			goPreOrder(type) {
				let orderStore = uni.getStorageSync('orderStore') || {}  // 先获取缓存的数据
				orderStore.type = type   // 设置订单的类型
				uni.setStorageSync('orderStore', orderStore)
				uni.navigateTo({
					url: '/pages/preorder/preorder'
				})
			},
			load() {
				this.$request.get('/orders/selectAll', {
					status: '待接单'
				}).then(res => {
					this.orderList = res.data || []
				}),
				
				this.$request.get('/orders/selectAll', {
					status: '待收货'
				}).then(res => {
					this.orderList1 = res.data || []
				}),
				
				this.$request.get('/orders/selectAll', {
					acceptId: this.user.id
				}).then(res => {
					this.orderList2 = res.data || []
				}),
				
				this.$request.get('/transaction/selectAll', {
					userId: this.user.id
				}).then(res => {
					this.transaction = res.data || []
				})
			},
			addorder(item){
				this.form.name = item.title
				this.form.descr = item.content
				this.form.weight=item.otherinfo
				this.form.price =item.price
				this.form.userId =item.userId
				this.form.tid =item.acceptId
				this.form.status='待接单'
				this.form.addressId=item.addressId
				this.form.targetId=item.targetId
				this.form.type='二手交易'
				this.$request.post('/orders/addOrder', this.form).then(res => {
						if (res.code === '200') {
							uni.showToast({
								icon: 'success',
								title: '成功',
								
							})
							this.del(item)
							this.load()
							
						} else {
							uni.showToast({
								icon: 'none',
								title: res.msg
							})
						}
					})	
				.catch(error => {
					console.error(error)
				})
				
			},
			del(item) {
				this.$request.del('/transaction/delete/' + item.id).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			},
			loadNotice() { 
				this.$request.get('/notice/selectAll').then(res => {
					this.noticeList = res.data || []
					
					let i = 0
					this.content = this.noticeList.length ? this.noticeList[i].content : ''
					
					// 切换展示公告内容
					if (this.noticeList.length > 1) {
						this.inter = setInterval(() => {
							i++
							if (i === this.noticeList.length) {
								i = 0
							}
							this.content = this.noticeList[i].content
						}, 5000)
					}
				
				})
			},
			arrive(orders) {
				orders.status = '待收货'
				this.$request.put('/orders/update', orders).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			},
			changeStatus(orders,status) {
				orders.status = status
				this.$request.put('/orders/update', orders).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功'
						})
						this.load()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				})
			}
			
		}
	}
</script>

<style>
	.cartegory-item {
		flex: 1; 
		display: flex; 
		justify-content: space-between; 
		align-items: center; 
		flex-direction: column; 
		grid-gap: 20rpx;
	}
</style>
