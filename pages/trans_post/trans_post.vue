<template>
	<view style="padding: 20rpx;">
		<view class="box" style="padding: 50rpx 20rpx;">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="140rpx" label-align="right">
				<uni-forms-item label="物品名称" name="title">
					<uni-easyinput type="textarea" v-model="form.title" placeholder="请输入标题" />
				</uni-forms-item>
				<uni-forms-item label="物品描述:" name="content" >
					<uni-easyinput type="textarea" v-model="form.content" placeholder="请输入内容" />
				</uni-forms-item>
				<uni-forms-item label="物品重量:" name="weight" >
					<uni-easyinput type="textarea" v-model="form.otherinfo" placeholder="请输入" />
				</uni-forms-item>
				<uni-forms-item label="物品价格:" name="price" >
					<uni-easyinput type="textarea" v-model="form.price" placeholder="请输入" />
				</uni-forms-item>
				
				<view class="box" style="margin-bottom: 10rpx;" @click="selectAddress('取货')">
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
				
				<view style="margin-top: 20rpx;">
					<button type="primary" class="my-button-primary" @click="save">提 交</button>
				</view>
			</uni-forms>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				form: {},
				rules: {
					title: {
						rules: [{
							required: true,
							errorMessage: '请输入物品名',
						}]
					},
					content: {
						rules: [{
							required: true,
							errorMessage: '请填写物品描述',
						}]
					},
					weight: {
						rules: [{
							required: true,
							errorMessage: '请填写物品重量',
						}]
					},
					price: {
						rules: [{
							required: true,
							errorMessage: '请填写物品价格',
						}]
					},
					
				
				},
				orderTypes: [
					{ value: '代拿快递', text: "代拿快递" },
					{ value: '代取餐品', text: "代取餐品" },
					{ value: '代买零食', text: "代买零食" },
					{ value: '其他跑腿', text: "其他跑腿" },
				],
				orders: {},
				user: uni.getStorageSync('xm-user'),
				form: { price: 1, type: '' },
				pickAddress: {},
				recieveAddress: {}
			}
		},
			onShow() {
				let orderStore = uni.getStorageSync('orderStore')
				this.form.type = orderStore?.type
				this.pickAddress = orderStore?.pickAddress || {}
				this.recieveAddress = orderStore?.recieveAddress || {}

			},
		methods: {
			save() {
				this.form.userId = this.user.id
				this.form.addressId=this.pickAddress.id
				this.$request.post('/transaction/add', this.form).then(res => {
					if (res.code === '200') {
						uni.showToast({
							icon: 'success',
							title: '操作成功',
							
					
						})
						uni.$emit('postAdded');
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
					// 延时跳转
					setTimeout(() => {
						uni.navigateBack()
					}, 100)
				})
			},
			selectAddress(addressType) {
				uni.navigateTo({
					url: '/pages/trans_address/trans_address?addressType=' + addressType
				})
			},
	
		}
	}
</script>

<style>

</style>