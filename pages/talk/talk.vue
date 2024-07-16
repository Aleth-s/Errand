<template>
	<view style="padding: 20rpx;">
		<view class="box" style="margin-bottom: 10rpx;" @click="selectAddress()">
			<uni-section title="取货地址" type="line"></uni-section>
			<view v-if="pickAddress.id">
				<view style="font-weight: bold; font-size: 32rpx; margin-bottom: 10rpx;">{{ pickAddress.address + pickAddress.doorNo }}</view>
				<view style="color: #888; margin-bottom: 10rpx;">
					<text style="margin-right: 20rpx;">{{ pickAddress.userName }}</text>
					<text>{{ pickAddress.phone }}</text>
				</view>
			</view>
			<view style="color: #888;" v-else>请选择取货地址</view>
		</view>		
		<view class="box">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="160rpx" label-align="right" validateTrigger="blur">
			  <uni-forms-item label="留言" name="talk" required>
			    <uni-easyinput type="text" v-model="form.talk" placeholder="请输入" />
			  </uni-forms-item>
			 
			</uni-forms>
			<view>
				<button type="primary" @click="addOrder" class="my-button-primary">提交订单</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				form: { },  // 表单绑定的对象
				rules: {
				  talk: {
				    rules: [{
				      required: true,
				      errorMessage: '请填写留言',
				    }]
				  },
				},
				imgs: [],
				imageStyles: {
				  "height": 80,	// 边框高度
				  "width": 80,	// 边框宽度
				  "border":{ // 如果为 Boolean 值，可以控制边框显示与否
				    "color":"#eee",		// 边框颜色
				    "width":"1px",		// 边框宽度
				    "style":"solid", 	// 边框样式
				    "radius":"50%" 		// 边框圆角，支持百分比
				  }
				},
				orderTypes: [
					{ value: '代拿快递', text: "代拿快递" },
					{ value: '代取餐品', text: "代取餐品" },
					{ value: '代买零食', text: "代买零食" },
					{ value: '其他跑腿', text: "其他跑腿" },
				],
				pickAddress: {},
				recieveAddress: {},
				user: uni.getStorageSync('xm-user'),
				tid:0
				
			}
		},
		onLoad(option) {
			this.tid = parseInt(option.tid)
			console.log("2121 "+this.tid);
		},
		onShow(option) {
			
			let orderStore1 = uni.getStorageSync('orderStore1')
			this.form.type = orderStore1?.type
			this.pickAddress = orderStore1?.pickAddress || {}
			this.recieveAddress = orderStore1?.recieveAddress || {}
			
		},
		methods: {
			selectAddress() {
				uni.navigateTo({
					url: '/pages/taddress/taddress'
				})
			},
			addOrder() {
				if (!this.pickAddress.id) {
					uni.showToast({
						icon: 'none',
						title: '请设置取货地址'
					})
					return
				}
				this.form.id=parseInt(this.tid)
				this.form.acceptId = this.user.id
				this.form.targetId = this.pickAddress.id
				this.$refs.formRef.validate().then(res => {
					this.$request.put('/transaction/update', this.form).then(res => {
						if (res.code === '200') {
							uni.showToast({
								icon: 'success',
								title: '下单成功'
							})
							// 清除缓存
							uni.removeStorageSync('orderStore1')
							
							setTimeout(() => {
								uni.redirectTo({
									url: '/pages/transaction/transaction'
								})
							}, 200)
						} else {
							uni.showToast({
								icon: 'none',
						
							})
						}
					})	
				})
				.catch(error => {
					console.error(error)
				})
			},

		}
	}
</script>

<style>

</style>
