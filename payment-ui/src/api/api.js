import axios from 'axios'
const PAYMENT_URL = '/api/payment'

export const checkHealth = () => {
  return axios.get(PAYMENT_URL.concat('/health'))
}
