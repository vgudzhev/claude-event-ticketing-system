<script setup lang="ts">
import type { Event } from '../api/ticketing'

const props = defineProps<{ event: Event }>()
const emit = defineEmits<{ buy: [] }>()

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('en-GB', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

function formatPrice(price: number): string {
  return new Intl.NumberFormat('en-IE', { style: 'currency', currency: 'EUR' }).format(price)
}
</script>

<template>
  <div class="bg-white rounded-xl shadow-md p-6 flex flex-col gap-4">
    <div class="flex items-start justify-between gap-2">
      <h2 class="text-lg font-semibold text-gray-900 leading-tight">{{ props.event.title }}</h2>
      <span
        v-if="props.event.totalTickets === 0"
        class="shrink-0 text-xs font-medium bg-gray-100 text-gray-500 px-2 py-1 rounded-full"
      >
        Sold Out
      </span>
    </div>

    <div class="text-sm text-gray-500 space-y-1">
      <p>
        <span class="font-medium text-gray-700">Date:</span>
        {{ formatDate(props.event.date) }}
      </p>
      <p>
        <span class="font-medium text-gray-700">Price:</span>
        {{ formatPrice(props.event.price) }}
      </p>
      <p>
        <span class="font-medium text-gray-700">Tickets remaining:</span>
        {{ props.event.totalTickets }}
      </p>
    </div>

    <button
      :disabled="props.event.totalTickets === 0"
      @click="emit('buy')"
      class="mt-auto w-full py-2 px-4 rounded-lg text-sm font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2"
      :class="
        props.event.totalTickets === 0
          ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
          : 'bg-indigo-600 hover:bg-indigo-700 text-white focus:ring-indigo-500'
      "
    >
      {{ props.event.totalTickets === 0 ? 'Sold Out' : 'Buy Ticket' }}
    </button>
  </div>
</template>
