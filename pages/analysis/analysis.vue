<template>
    <view style="padding: 20rpx;">
        <view style="margin-bottom: 4
		
		rpx;" class="box">
            <uni-segmented-control :current="current" :values="items" @clickItem="onClickItem" styleType="text"
                activeColor="#006eff"></uni-segmented-control>
        </view>
        <view class="box">
            <view class="line" style="margin-bottom: 40rpx;">
                <view class="line-title line-title-blue" style="color:blue;">总订单数：</view>
                <view class="line-content">{{ count }}</view>
                <view class="line-title line-title-blue" style="color:blue;">总收入：</view>
                <view class="line-content">{{ sum }}¥</view>
            </view>
        </view>
        <view class="box">
            <view>
                <image :src="wordcloudImageUrl" style="width:460" @click="showModal = true"></image>
            </view>
    
        </view>
		<view class="box" style="margin-top: 15px;">
		    <view>
		        <image :src="wordcloudImageUrl1" style="width:460" @click="showModal = true"></image>
		    </view>
		  
		</view>
		

 <!--       <uni-popup type="center" :show="showModal" @close="showModal = false">
            <view>
                <image :src="wordcloudImageUrl" style="width: 100%; height: auto;"></image>
                <view style="text-align: center; margin-top: 20rpx;">
                    <button type="primary" @click="showModal = false">关闭</button>
                </view>
            </view>
        </uni-popup> -->
    </view>
</template>

<script>
export default {
    data() {
        return {
            items: ['近一周', '近六个月', '近三年'],
            current: '近一周',
            records: {},
            showInfo: false,
            coo: false,
            id: 0,
            wordcloudImageUrl: '',
			wordcloudImageUrl1: '',
            word: '',
            user: uni.getStorageSync('xm-user'),
            count: 0,
            sum: 0,
            showModal: false ,// 控制模态框显示
			 futureIncome: [] // 新增未来收入预测数据
        }
    },
    onShow() {
        this.load()
		this.loadFutureIncome()
    },
    methods: {
        back() {
            uni.navigateTo({})
        },
        onClickItem(e) {
            this.current = this.items[e.currentIndex]
            this.load()
        },
        load() {
            let id = this.user.id
            let param1 = '7'
            let param2 = '6'
            if (this.current == '近一周') {
                this.$request.get('/records/analysis/' + id + '/' + param1)
                    .then(res => {
                        this.wordcloudImageUrl = 'data:image/png;base64,' + res.data.image;
                        this.records = res.data
                        this.count = res.data.id
                        this.sum = res.data.total
                    })
                    .catch(error => {
                        console.error('Failed to load order:', error);
                    });
					
					this.$request.get('/records/predictIncome/' + id)
					    .then(res => {
					        this.wordcloudImageUrl1 = 'data:image/png;base64,' + res.data.image;
					
					    })
					    .catch(error => {
					        console.error('Failed to load order:', error);
					    });
					
            }
            if (this.current == '近六个月') {
                this.$request.get('/records/analysis2/' + id + '/' + param2)
                    .then(res => {
                        this.wordcloudImageUrl = 'data:image/png;base64,' + res.data.image;
                        this.records = res.data
                        this.count = res.data.id
                        this.sum = res.data.total
                    })
                    .catch(error => {
                        console.error('Failed to load order:', error);
                    });
            }
            if (this.current == '近三年') {
                this.$request.get('/records/analysis3/' + id + '/' + param2)
                    .then(res => {
                        this.wordcloudImageUrl = 'data:image/png;base64,' + res.data.image;
                        this.records = res.data
                        this.count = res.data.id
                        this.sum = res.data.total
                    })
                    .catch(error => {
                        console.error('Failed to load order:', error);
                    });
            }
        },
		loadFutureIncome() {
		            let id = this.user.id
		            this.$request.get('/records/predictIncome/' + id)
		                .then(res => {
		                    this.futureIncome = res.data
		                })
		                .catch(error => {
		                    console.error('Failed to load future income:', error);
		                });
		        }
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

.my-button-primary {
    width: 100%;
    margin-top: 20rpx;
}
</style>
