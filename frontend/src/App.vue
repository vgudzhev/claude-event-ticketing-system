<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEvents } from './api/ticketing'
import type { Event } from './api/ticketing'
import EventCard from './components/EventCard.vue'
import PurchaseModal from './components/PurchaseModal.vue'

const events = ref<Event[]>([])
const selectedEvent = ref<Event | null>(null)
const fetchError = ref<string | null>(null)

async function loadEvents() {
  fetchError.value = null
  try {
    const res = await getEvents()
    events.value = res.data
  } catch {
    fetchError.value = 'Failed to load events. Make sure the backend is running on port 8080.'
  }
}

function openModal(event: Event) {
  selectedEvent.value = event
}

function closeModal() {
  selectedEvent.value = null
}

async function onPurchased() {
  await loadEvents()
}

onMounted(loadEvents)
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm">
      <div class="max-w-6xl mx-auto px-4 py-5 flex items-center gap-3">
        <span class="text-2xl">🎟️</span>
        <h1 class="text-2xl font-bold text-gray-900">Event Ticketing</h1>
      </div>
    </header>

    <!-- Main content -->
    <main class="max-w-6xl mx-auto px-4 py-8">
      <!-- Error banner -->
      <div
        v-if="fetchError"
        class="mb-6 rounded-xl bg-red-50 border border-red-200 text-red-700 px-5 py-4 text-sm"
      >
        {{ fetchError }}
      </div>

      <!-- Empty state -->
      <div
        v-else-if="events.length === 0"
        class="text-center py-24 text-gray-400 text-sm"
      >
        Loading events…
      </div>

      <!-- Events grid -->
      <div
        v-else
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        <EventCard
          v-for="event in events"
          :key="event.id"
          :event="event"
          @buy="openModal(event)"
        />
      </div>
    </main>

    <!-- Purchase modal -->
    <PurchaseModal
      v-if="selectedEvent"
      :event="selectedEvent"
      @close="closeModal"
      @purchased="onPurchased"
    />
  </div>
</template>
