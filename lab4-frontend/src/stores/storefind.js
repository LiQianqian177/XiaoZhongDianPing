import { defineStore } from 'pinia'

export const useSearchStore = defineStore('search', {
  state: () => ({
    results: [],
    showResults: false,
    keyword: '',
    filters: {
      sortBy: 'default',
      minRating: 0,
      minPrice: null,
      maxPrice: null,
      minCost: null,
      maxCost: null
    }
  }),
  actions: {
    setResults(results) {
      this.results = results
      this.showResults = true
    },
    setKeyword(keyword) {
      this.keyword = keyword
    },
    setFilters(filters) {
      this.filters = { ...this.filters, ...filters }
    }
  }
})
