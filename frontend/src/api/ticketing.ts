import axios from 'axios'

export interface Event {
  id: number
  title: string
  date: string
  totalTickets: number
  price: number
}

export interface Ticket {
  id: number
  eventId: number
  customerName: string
  purchaseDate: string
}

const http = axios.create({ baseURL: '/api' })

export const getEvents = () => http.get<Event[]>('/events')

export const purchaseTicket = (eventId: number, customerName: string) =>
  http.post<Ticket>('/tickets/purchase', { eventId, customerName })
