<template>
  <div class="relative">
    <Listbox
      v-model="selectedChairs"
      multiple
      class="relative"
      :disabled="loading"
    >
      <div class="relative mt-1">
        <ListboxButton
          class="relative w-full cursor-default rounded-lg bg-white py-2 pl-3 pr-10 text-left border focus:outline-none focus-visible:border-indigo-500 focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75 focus-visible:ring-offset-2 focus-visible:ring-offset-orange-300 sm:text-sm disabled:bg-gray-100 disabled:cursor-not-allowed"
        >
          <span v-if="loading" class="block truncate text-gray-500">
            Loading chairs...
          </span>
          <template v-else>
            <span class="block truncate" v-if="selectedChairs.length === 0">
              Select Chairs
            </span>
            <span class="block truncate" v-else>
              {{ selectedChairs.length }} chair(s) selected
            </span>
          </template>
          <span class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
            <ChevronUpDownIcon
              class="h-5 w-5 text-gray-400"
              aria-hidden="true"
            />
          </span>
        </ListboxButton>

        <transition
          leave-active-class="transition duration-100 ease-in"
          leave-from-class="opacity-100"
          leave-to-class="opacity-0"
        >
          <ListboxOptions
            class="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm z-10"
          >
            <ListboxOption
              v-for="chair in chairs"
              :key="chair.id"
              v-slot="{ active, selected }"
              :value="chair"
              as="template"
            >
              <li
                :class="[
                  active ? 'bg-indigo-100 text-indigo-900' : 'text-gray-900',
                  'relative cursor-default select-none py-2 pl-10 pr-4',
                ]"
              >
                <span
                  :class="[
                    selected ? 'font-medium' : 'font-normal',
                    'block truncate',
                  ]"
                >
                  {{ chair.name }}
                </span>
                <span
                  v-if="selected"
                  :class="[
                    active ? 'text-indigo-600' : 'text-indigo-600',
                    'absolute inset-y-0 left-0 flex items-center pl-3',
                  ]"
                >
                  <CheckIcon class="h-5 w-5" aria-hidden="true" />
                </span>
              </li>
            </ListboxOption>
          </ListboxOptions>
        </transition>
      </div>
    </Listbox>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import {
  Listbox,
  ListboxButton,
  ListboxOptions,
  ListboxOption,
} from '@headlessui/vue'
import { ChevronUpDownIcon, CheckIcon } from '@heroicons/vue/20/solid'

interface Chair {
  id: number
  name: string
}

interface Props {
  chairs: Chair[]
  modelValue: Chair[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits(['update:modelValue'])

const selectedChairs = ref<Chair[]>(props.modelValue)

// Debounced emit for better performance
let timeout: NodeJS.Timeout
watch(selectedChairs, (newValue) => {
  clearTimeout(timeout)
  timeout = setTimeout(() => {
    emit('update:modelValue', newValue)
  }, 300)
})
</script>
