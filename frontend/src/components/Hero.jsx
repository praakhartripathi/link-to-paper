import React from 'react';

export default function Hero() {
    return (
        <div className="text-center animate-fade-in">
            {/* Badge */}
            <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full mb-6
                      border border-indigo-500/30 bg-indigo-500/10 text-indigo-300
                      text-xs font-semibold tracking-widest uppercase">
                <span className="w-1.5 h-1.5 rounded-full bg-indigo-400 animate-pulse" />
                AI-Powered Research Tool
            </div>

            {/* Title */}
            <h1 className="text-5xl md:text-7xl font-black tracking-tight mb-4 leading-none">
                <span className="gradient-text">Link2Paper</span>
                <span className="block text-3xl md:text-4xl font-light text-slate-400 mt-2">
                    AI Technical Paper Generator
                </span>
            </h1>

            {/* Subtitle */}
            <p className="mt-5 max-w-xl mx-auto text-slate-400 text-base md:text-lg leading-relaxed">
                Paste any webpage URL. Our AI scrapes, cleans, and transforms the content into
                a structured technical paper — ready to download as a{" "}
                <span className="text-indigo-400 font-medium">formatted PDF</span>.
            </p>

            {/* Feature pills */}
            <div className="flex flex-wrap justify-center gap-2 mt-6">
                {['Web Scraping', 'AI Processing', 'PDF Export', 'APA / IEEE Ready'].map(tag => (
                    <span
                        key={tag}
                        className="px-3 py-1 rounded-full text-xs font-medium text-slate-400
                       border border-slate-700 bg-slate-800/50"
                    >
                        {tag}
                    </span>
                ))}
            </div>
        </div>
    );
}
