<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-6">
      <h1 class="text-3xl font-bold mb-4">Topics</h1>
      <div class="flex gap-4">
        <div class="w-64">
          <ChairFilter
            v-model="selectedChairs"
            :chairs="chairs"
            :loading="isLoading"
          />
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div
      v-if="error"
      class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative mb-4"
      role="alert"
    >
      <span class="block sm:inline">{{ error }}</span>
    </div>

    <!-- Loading State -->
    <div
      v-if="isLoading"
      class="flex justify-center items-center py-8"
    >
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500"></div>
    </div>

    <!-- Topics list -->
    <div
      v-else
      class="grid gap-4"
    >
      <div
        v-for="topic in filteredTopics"
        :key="topic.id"
        class="p-4 border rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200"
      >
        <h2 class="text-xl font-semibold mb-2">{{ topic.title }}</h2>
        <div class="text-sm text-gray-600">
          Chair: {{ getChairName(topic.chairId) }}
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-if="filteredTopics.length === 0 && !isLoading"
        class="text-center py-8 text-gray-500"
      >
        {{ selectedChairs.length > 0 ? 'No topics found for selected chairs' : 'No topics available' }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue'
import ChairFilter from '../components/filters/ChairFilter.vue'

interface Chair {
  id: number
  name: string
}

interface Topic {
  id: number
  title: string
  chairId: number
}

export default defineComponent({
  name: 'LandingPage',
  components: {
    ChairFilter
  },
  setup() {
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    const chairs = ref<Chair[]>([
      { id: 1, name: 'Chair A' },
      { id: 2, name: 'Chair B' },
      { id: 3, name: 'Chair C' },
    ])

    const topics = ref<Topic[]>([
      { id: 1, title: 'Topic 1', chairId: 1 },
      { id: 2, title: 'Topic 2', chairId: 2 },
      { id: 3, title: 'Topic 3', chairId: 1 },
    ])

    const selectedChairs = ref<Chair[]>([])

    const filteredTopics = computed(() => {
      if (selectedChairs.value.length === 0) {
        return topics.value
      }
      
      return topics.value.filter((topic) =>
        selectedChairs.value.some((chair) => chair.id === topic.chairId)
      )
    })

    const getChairName = (chairId: number): string => {
      const chair = chairs.value.find(c => c.id === chairId)
      return chair?.name || 'Unknown Chair'
    }

    return {
      chairs,
      topics,
      selectedChairs,
      filteredTopics,
      isLoading,
      error,
      getChairName
    }
  }
})
</script>