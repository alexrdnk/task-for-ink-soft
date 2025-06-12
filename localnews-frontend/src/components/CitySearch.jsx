import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { X, Search } from 'lucide-react';
import './CitySearch.css';

export default function CitySearch({ onSelectCity }) {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const wrapperRef = useRef(null);
  const debounceRef = useRef(null);

  // Fetch suggestions with debounce
  useEffect(() => {
    if (query.length < 1) {
      setSuggestions([]);
      setShowDropdown(false);
      return;
    }
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      axios.get(`/api/cities?query=${encodeURIComponent(query)}`)
        .then(res => {
          setSuggestions(res.data.slice(0, 5));
        })
        .catch(err => console.error('Error fetching city suggestions:', err));
    }, 300);
  }, [query]);

  // Show dropdown when suggestions are available
  useEffect(() => {
    setShowDropdown(suggestions.length > 0);
  }, [suggestions]);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = e => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  const handleSelect = city => {
    setQuery(city);
    onSelectCity(city);
    setShowDropdown(false);
  };

  const handleKeyDown = e => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (query.trim()) {
        onSelectCity(query.trim());
        setShowDropdown(false);
      }
    }
  };

  const clearSearch = () => {
    setQuery('');
    setSuggestions([]);
    onSelectCity('');
  };

  const renderSuggestion = city => {
    const idx = city.toLowerCase().indexOf(query.toLowerCase());
    if (idx === -1) return city;
    const before = city.slice(0, idx);
    const match = city.slice(idx, idx + query.length);
    const after = city.slice(idx + query.length);
    return (
      <span>
        {before}<span className="highlight">{match}</span>{after}
      </span>
    );
  };

  return (
    <div className="city-search" ref={wrapperRef}>
      <div className="input-wrapper">
        <Search className="icon search-icon" />
        <input
          type="text"
          placeholder="Search city..."
          value={query}
          onChange={e => setQuery(e.target.value)}
          className="city-input"
          onFocus={() => suggestions.length > 0 && setShowDropdown(true)}
          onKeyDown={handleKeyDown}
        />
        {query && (
          <button type="button" className="clear-btn" onClick={clearSearch}>
            <X className="icon clear-icon" />
          </button>
        )}
      </div>
      {showDropdown && (
        <ul className="suggestions-list">
          {suggestions.map(city => (
            <li
              key={city}
              className="suggestion-item"
              onClick={() => handleSelect(city)}
            >
              {renderSuggestion(city)}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
