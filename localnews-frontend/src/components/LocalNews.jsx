import React, { useEffect, useState } from 'react';
import { MapPin, Calendar, ExternalLink } from 'lucide-react';
import axios from 'axios';

export default function LocalNews({ city }) {
  const [articles, setArticles] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!city) return;

    setIsLoading(true);
    // Introduce a delay to simulate network latency for better visual feedback of loading state
    const timer = setTimeout(() => {
      axios.get(`/api/articles/local/${encodeURIComponent(city)}`)
        .then(res => {
          setArticles(res.data);
          setIsLoading(false);
        })
        .catch(error => {
          console.error('Error fetching local news:', error);
          setIsLoading(false);
        });
    }, 500); // 500ms delay

    return () => clearTimeout(timer); // Clear timeout if component unmounts or city changes
  }, [city]);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

    if (diffHours < 1) return 'Just now';
    if (diffHours < 24) return `${diffHours}h ago`;
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  };

  if (isLoading) {
    return (
      <div className="card w-full col-span-full md:col-span-1">
        <div className="flex items-center space-x-3 mb-6">
          <MapPin className="w-7 h-7 text-secondary" />
          <h2 className="text-2xl font-bold text-gray-800">Local News for {city}</h2>
        </div>
        <div className="space-y-5">
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} className="animate-pulse p-4 rounded-xl border border-gray-100">
              <div className="h-5 bg-gray-200 rounded w-3/4 mb-2"></div>
              <div className="h-3 bg-gray-200 rounded w-1/3"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="card w-full col-span-full md:col-span-1">
      <div className="flex items-center space-x-3 mb-6">
        <MapPin className="w-7 h-7 text-secondary" />
        <h2 className="text-2xl font-bold text-gray-800">Local News for {city}</h2>
      </div>

      <div className="space-y-4">
        {articles.length > 0 ? (
          articles.map((article) => (
            <article key={article.id} className="group">
              <a
                href={article.url}
                target="_blank"
                rel="noopener noreferrer"
                className="block p-4 rounded-xl hover:bg-gray-50 transition-all duration-200 border border-transparent hover:border-gray-200"
              >
                <h3 className="font-semibold text-gray-800 group-hover:text-primary transition-colors duration-200 mb-2 leading-snug text-lg">
                  {article.title}
                </h3>
                <div className="flex items-center space-x-3 text-sm text-gray-500">
                  <Calendar className="w-4 h-4" />
                  <span>{formatDate(article.publishedAt)}</span>
                  <ExternalLink className="w-4 h-4 text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity duration-200" />
                </div>
              </a>
            </article>
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            <MapPin className="w-16 h-16 mx-auto mb-4 text-gray-300" />
            <p className="text-lg">No local news found for <span className="font-semibold text-primary">{city}</span>. Try another city!</p>
          </div>
        )}
      </div>
    </div>
  );
}