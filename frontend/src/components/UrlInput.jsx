import React, { useState } from 'react';

const PLACEHOLDER_URLS = [
    'https://en.wikipedia.org/wiki/Machine_learning',
    'https://en.wikipedia.org/wiki/Artificial_intelligence',
    'https://en.wikipedia.org/wiki/Quantum_computing',
];

export default function UrlInput({ onGenerate, isLoading }) {
    const [url, setUrl] = useState('');
    const [error, setError] = useState('');

    const validate = (val) => {
        try {
            const u = new URL(val);
            return u.protocol === 'http:' || u.protocol === 'https:';
        } catch {
            return false;
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!url.trim()) {
            setError('Please enter a URL.');
            return;
        }
        if (!validate(url.trim())) {
            setError('Please enter a valid https:// URL.');
            return;
        }
        setError('');
        onGenerate(url.trim());
    };

    const useExample = (exUrl) => {
        setUrl(exUrl);
        setError('');
    };

    return (
        <div className="w-full max-w-2xl mx-auto animate-slide-up" style={{ animationDelay: '0.1s' }}>
            {/* Input card */}
            <div className="glass-card rounded-2xl p-6">
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* URL field */}
                    <div>
                        <label htmlFor="url-input" className="block text-xs font-semibold text-slate-400 mb-2 tracking-widest uppercase">
                            Website URL
                        </label>
                        <div className="flex gap-3">
                            <input
                                id="url-input"
                                type="url"
                                value={url}
                                onChange={(e) => { setUrl(e.target.value); setError(''); }}
                                placeholder="https://en.wikipedia.org/wiki/Neural_network"
                                className="url-input flex-1 rounded-xl px-4 py-3 text-sm font-medium"
                                disabled={isLoading}
                                autoComplete="off"
                            />
                            <button
                                id="generate-btn"
                                type="submit"
                                disabled={isLoading || !url.trim()}
                                className="btn-primary rounded-xl px-6 py-3 text-sm font-bold text-white
                           whitespace-nowrap min-w-[140px] flex items-center justify-center gap-2"
                            >
                                {isLoading ? (
                                    <>
                                        <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                        Generating…
                                    </>
                                ) : (
                                    <>
                                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                        </svg>
                                        Generate Paper
                                    </>
                                )}
                            </button>
                        </div>
                        {error && (
                            <p className="mt-2 text-xs text-red-400 flex items-center gap-1.5">
                                <svg className="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 20 20">
                                    <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                                </svg>
                                {error}
                            </p>
                        )}
                    </div>

                    {/* Example URLs */}
                    <div>
                        <p className="text-xs text-slate-500 mb-2">Try an example:</p>
                        <div className="flex flex-wrap gap-2">
                            {PLACEHOLDER_URLS.map((u) => {
                                const label = decodeURIComponent(u.split('/').pop().replaceAll('_', ' '));
                                return (
                                    <button
                                        key={u}
                                        type="button"
                                        onClick={() => useExample(u)}
                                        disabled={isLoading}
                                        className="text-xs px-2.5 py-1 rounded-lg border border-slate-700 bg-slate-800/60
                               text-slate-400 hover:text-indigo-300 hover:border-indigo-500/50
                               transition-all duration-200 disabled:opacity-40"
                                    >
                                        {label}
                                    </button>
                                );
                            })}
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}
