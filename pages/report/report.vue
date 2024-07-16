<template>
	<view style="padding: 20rpx;">
	
		<view class="box">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="160rpx" label-align="right" validateTrigger="blur">
			  <uni-forms-item label="举报内容" name="talk" required>
			    <uni-easyinput type="text" v-model="form.content" placeholder="请输入" />
			  </uni-forms-item>
			 
			 <uni-forms-item label="举报类型" name="type" required>
			 				  <uni-data-select v-model="form.type" :localdata="orderTypes"></uni-data-select>
			 </uni-forms-item>
			</uni-forms>
			<view>
				<button type="primary" @click="addOrder" class="my-button-primary">提交举报</button>
			</view>
		</view>
	</view>
</template>

<script>
import { loadEnvFile } from 'process'
	export default {
		data() {
			return {
				form: { },  // 表单绑定的对象
				rules: {
				  content: {
				    rules: [{
				      required: true,
				      errorMessage: '请填写举报理由',
				    }]
				  },
				  type: {
				    rules: [{
				      required: true,
				      errorMessage: '请设置举报类型',
				    }]
				  }
				},
				user: uni.getStorageSync('xm-user'),
				tid:0,
				orderList:{},
				orderTypes: [
					{ value: '展示物品与实物不符', text: "展示物品与实物不符" },
					{ value: '物品破损', text: "物品破损" },
					{ value: '物品与描述不符', text: "物品与描述不符" },
				
				]
				
			}
		},
		onLoad(option) {
			this.tid = parseInt(option.tid)
			this.load()
		
		},
	
		methods: {
			selectAddress() {
				uni.navigateTo({
					url: '/pages/orders/orders'
				})
			},
			load() {
				let params = {
					id: this.tid
				}
			  this.$request.get('/orders/selectAll', params).then(res => {
			  	this.orderList = res.data || []
			  })
			},
			addOrder() {
				
				this.form.userId=this.user.id
				this.form.targetId=this.orderList[0].tid
				this.form.orderId=this.tid
				this.$refs.formRef.validate().then(res => {
					this.$request.post('/report/add', this.form).then(res => {
						if (res.code === '200') {
							uni.showToast({
								icon: 'success',
								title: '举报成功'
							})
							// 
							
							setTimeout(() => {
								uni.redirectTo({
									url: '/pages/orders/orders'
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
