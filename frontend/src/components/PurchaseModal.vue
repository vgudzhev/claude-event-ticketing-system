<script setup lang="ts">
import { ref } from 'vue'
import type { Event } from '../api/ticketing'
import { purchaseTicket } from '../api/ticketing'

const props = defineProps<{ event: Event }>()
const emit = defineEmits<{ close: []; purchased: [] }>()

const customerName = ref('')
const loading = ref(false)
const error = ref<string | null>(null)
const success = ref(false)

function onBackdropClick() {
  if (!loading.value) emit('close')
}

async function onSubmit() {
  if (!customerName.value.trim()) return
  loading.value = true
  error.value = null

  try {
    await purchaseTicket(props.event.id, customerName.value.trim())
    success.value = true
    setTimeout(() => {
      emit('purchased')
      emit('close')
    }, 2000)
  } catch (err: any) {
    const msg = err?.response?.data?.message
    error.value = msg ?? 'Something went wrong. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <!-- Backdrop -->
  <div
    class="fixed inset-0 z-40 bg-black/50 flex items-center justify-center p-4"
    @click.self="onBackdropClick"
  >
    <!-- Modal card -->
    <div class="relative z-50 bg-white rounded-xl shadow-xl w-full max-w-md p-6 flex flex-col gap-5">
      <!-- Header -->
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="text-xl font-bold text-gray-900">Buy Ticket</h2>
          <p class="text-sm text-gray-500 mt-0.5">{{ props.event.title }}</p>
        </div>
        <button
          @click="emit('close')"
          :disabled="loading"
          class="text-gray-400 hover:text-gray-600 disabled:opacity-40 transition-colors text-xl leading-none"
          aria-label="Close"
        >
          ✕
        </button>
      </div>

      <!-- Success state -->
      <div v-if="success" class="rounded-lg bg-green-50 border border-green-200 text-green-700 px-4 py-3 text-sm font-medium">
        Ticket purchased successfully! 🎉
      </div>

      <!-- Form -->
      <form v-else @submit.prevent="onSubmit" class="flex flex-col gap-4">
        <div class="flex flex-col gap-1.5">
          <label for="customerName" class="text-sm font-medium text-gray-700">Your name</label>
          <input
            id="customerName"
            v-model="customerName"
            type="text"
            placeholder="e.g. Jane Smith"
            required
            :disabled="loading"
            class="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent disabled:bg-gray-50 disabled:text-gray-400"
          />
        </div>

        <!-- Error message -->
        <p v-if="error" class="rounded-lg bg-red-50 border border-red-200 text-red-600 px-4 py-3 text-sm">
          {{ error }}
        </p>

        <button
          type="submit"
          :disabled="loading || !customerName.trim()"
          class="w-full py-2 px-4 rounded-lg text-sm font-semibold bg-indigo-600 hover:bg-indigo-700 text-white transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Processing…' : 'Confirm Purchase' }}
        </button>
      </form>
    </div>
  </div>
</template>
