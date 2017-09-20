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
import { submitPayment } from '../api/api.js'
export default {
  name: 'PaymentForm',
  data () {
    return {
      msg: '2'
    }
  },
  methods: {
    submitPay () {
      this.msg = submitPayment('aaa')
    }
  }
}
</script>