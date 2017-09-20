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
export default {
  name: 'PaymentForm',
  data () {
    return {
      msg: '2'
    }
  },
  methods: {
    submitPay () {
      this.msg = 100
    }
  }
}
</script>