<template>
  <div class="relative">
    <div class="w-full">
      <div class="flex gap-2 items-end">
        <div class="flex-1">
          <label
            :for="id"
            class="block text-sm font-medium text-gray-700 mb-1"
          >
            Filter by Chair
          </label>
          <select
            :id="id"
            multiple
            v-model="selectedChairs"
            class="w-full rounded-lg border bg-white py-2 pl-3 pr-10 text-left focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500 sm:text-sm min-h-[120px]"
            :class="{ 'opacity-50 cursor-not-allowed': loading }"
            :disabled="loading"
            @keydown.escape="clearSelection"
          >
            <option
              v-for="chair in chairs"
              :key="chair.id"
              :value="chair"
              class="py-2 px-3 hover:bg-indigo-50 cursor-pointer"
            >
              {{ chair.name }}
            </option>
          </select>
        </div>
        <button
          v-if="selectedChairs.length > 0"
          @click="clearSelection"
          type="button"
          class="text-sm text-gray-500 hover:text-gray-700 py-2 px-3 rounded-md hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 mb-[1px]"
          :disabled="loading"
        >
          Clear
        </button>
      </div>
      <div class="mt-2 text-sm text-gray-500 flex justify-between items-center">
        <span>
          {{ selectionText }}
        </span>
        <span v-if="loading" class="text-indigo-600">
          Loading...
        </span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch, computed } from 'vue'

interface Chair {
  id: number
  name: string
}

export default defineComponent({
  name: 'ChairFilter',
  props: {
    chairs: {
      type: Array as () => Chair[],
      required: true
    },
    modelValue: {
      type: Array as () => Chair[],
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    // Generate unique ID for accessibility
    const id = `chair-filter-${Math.random().toString(36).substr(2, 9)}`

    const selectedChairs = ref<Chair[]>(props.modelValue)

    const selectionText = computed(() => {
      if (selectedChairs.value.length === 0) {
        return 'Select one or more chairs'
      }
      return `${selectedChairs.value.length} chair${selectedChairs.value.length === 1 ? '' : 's'} selected`
    })

    watch(selectedChairs, (newValue) => {
      emit('update:modelValue', newValue)
    })

    const clearSelection = () => {
      selectedChairs.value = []
    }

    return {
      id,
      selectedChairs,
      selectionText,
      clearSelection
    }
  }
})
</script>

<style scoped>
select option {
  padding: 8px;
  margin: 2px 0;
  border-radius: 4px;
}

select option:checked {
  background: linear-gradient(0deg, #EEF2FF 0%, #EEF2FF 100%);
  color: #4F46E5;
}

select:focus option:checked {
  background: linear-gradient(0deg, #EEF2FF 0%, #EEF2FF 100%);
  color: #4F46E5;
}
</style>