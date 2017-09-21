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
import { checkHealth } from '../api/api.js'
export default {
  name: 'PaymentForm',
  data () {
    return {
      msg: '2'
    }
  },
  methods: {
    submitPay () {
      checkHealth().then(res => {
        console.log(res.data)
        this.msg = res.data
        return
      })
    }
  }
}
</script>