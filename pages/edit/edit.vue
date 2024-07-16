<template>
	<view style="padding: 20rpx;">
		<view class="box" style="padding: 50rpx 20rpx;">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="140rpx" label-align="right">
				<uni-forms-item label="帖子标题" name="title">
					<uni-easyinput type="textarea" v-model="form.title" placeholder="请输入标题" />
				</uni-forms-item>
				<uni-forms-item label="帖子内容:" name="content" >
					<uni-easyinput type="textarea" v-model="form.content" placeholder="请输入内容" />
				</uni-forms-item>

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
							errorMessage: '请输入标题',
						}]
					},
					content: {
						rules: [{
							required: true,
							errorMessage: '请填写内容',
						}]
					}
				
				},
				orders: {},
				user: uni.getStorageSync('xm-user')
			}
		},

		methods: {
			save() {
				this.form.userId = this.user.id

				this.$request.post('/forum/add', this.form).then(res => {
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
					}, 500)
				})
			},
			
	
		}
	}
</script>

<style>

</style>