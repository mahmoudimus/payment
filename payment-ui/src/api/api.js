import axios from 'axios'
const PAYMENT_URL = '/api/payment'

export const createPayment = (params) => {
  return axios.post(PAYMENT_URL.concat('/create'), params)
}
