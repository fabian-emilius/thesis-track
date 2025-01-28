import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ChairFilter from '../ChairFilter.vue'

describe('ChairFilter', () => {
  const chairs = [
    { id: 1, name: 'Chair A' },
    { id: 2, name: 'Chair B' },
    { id: 3, name: 'Chair C' }
  ]

  it('renders properly with initial state', () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [],
        loading: false
      }
    })
    expect(wrapper.text()).toContain('Select one or more chairs')
    expect(wrapper.find('select').exists()).toBe(true)
  })

  it('shows number of selected chairs', async () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [chairs[0], chairs[1]],
        loading: false
      }
    })
    expect(wrapper.text()).toContain('2 chairs selected')
  })

  it('emits update:modelValue when selection changes', async () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [],
        loading: false
      }
    })

    await wrapper.find('select').setValue([chairs[0]])
    
    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    expect(emitted[0][0]).toEqual([chairs[0]])
  })

  it('shows loading state', () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [],
        loading: true
      }
    })
    expect(wrapper.text()).toContain('Loading')
    expect(wrapper.find('select').attributes('disabled')).toBeDefined()
  })

  it('clears selection when clear button is clicked', async () => {
    const wrapper = mount(ChairFilter, {
      props: {
        chairs,
        modelValue: [chairs[0]],
        loading: false
      }
    })

    await wrapper.find('button').trigger('click')
    
    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    expect(emitted[0][0]).toEqual([])
  })
})