<<template>
  <div>
        bizNo<input v-model="bizNo"/>
        accountNo<input v-model="accountNo"/>
	    <button @click="submitPay"> submit</button>
   <div>
        <p>{{ msg }} transaction</p>
  </div>
  </div>
</template>

<script>
import { createPayment } from '../api/api.js'
export default {
  name: 'PaymentForm',
  data () {
    return {
      bizNo: '',
      accountNo: '',
      msg: '2'
    }
  },
  methods: {
    submitPay () {
      let order = {
        'bizNo': this.bizNo,
        'accountNo': this.accountNo,
        'bizType': 'NB',
        'returnURL': null
      }

      createPayment(order).then(res => {
        console.log(res.data)
        this.msg = res.data
        return
      })
    }
  }
}
</script>