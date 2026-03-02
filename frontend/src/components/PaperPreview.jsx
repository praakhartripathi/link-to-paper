import React from 'react';
import DownloadButton from './DownloadButton';

const SECTIONS = [
    { key: 'abstract', label: 'Abstract' },
    { key: 'introduction', label: 'Introduction' },
    { key: 'methodology', label: 'Methodology' },
    { key: 'discussion', label: 'Discussion' },
    { key: 'conclusion', label: 'Conclusion' },
];

export default function PaperPreview({ pdfBlob, sourceUrl, onReset }) {
    return (
        <div className="w-full max-w-2xl mx-auto animate-slide-up">
            {/* Success banner */}
            <div className="flex items-center gap-3 p-4 rounded-xl bg-green-500/10 border border-green-500/30 mb-4">
                <div className="w-8 h-8 rounded-full bg-green-500/20 flex items-center justify-center shrink-0">
                    <svg className="w-4 h-4 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                    </svg>
                </div>
                <div className="min-w-0">
                    <p className="text-sm font-semibold text-green-300">Paper Generated Successfully!</p>
                    <p className="text-xs text-green-500 truncate">Source: {sourceUrl}</p>
                </div>
            </div>

            {/* Paper card */}
            <div className="glass-card rounded-2xl p-6">
                {/* Paper icon and title */}
                <div className="flex items-start gap-4 mb-5 pb-5 border-b border-slate-800">
                    <div className="w-12 h-12 rounded-xl bg-indigo-500/15 border border-indigo-500/30
                          flex items-center justify-center shrink-0 text-2xl">
                        📄
                    </div>
                    <div>
                        <h2 className="text-base font-bold text-slate-200">Technical Paper Ready</h2>
                        <p className="text-xs text-slate-500 mt-0.5">
                            Your AI-generated paper includes: Abstract · Introduction · Methodology ·
                            Discussion · Conclusion · References
                        </p>
                    </div>
                </div>

                {/* Section list */}
                <div className="space-y-3 mb-5">
                    {SECTIONS.map(({ key, label }, i) => (
                        <div key={key}
                            className="flex items-center gap-3 py-2 px-3 rounded-lg bg-slate-800/40 border border-slate-800
                            hover:border-indigo-500/20 transition-colors duration-200">
                            <span className="text-xs font-mono text-indigo-400 w-4 shrink-0">{i + 1}.</span>
                            <span className="text-sm text-slate-300">{label}</span>
                            <svg className="w-3.5 h-3.5 text-slate-600 ml-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                            </svg>
                        </div>
                    ))}
                    <div className="flex items-center gap-3 py-2 px-3 rounded-lg bg-slate-800/40 border border-slate-800">
                        <span className="text-xs font-mono text-indigo-400 w-4 shrink-0">6.</span>
                        <span className="text-sm text-slate-300">References</span>
                        <svg className="w-3.5 h-3.5 text-slate-600 ml-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                    </div>
                </div>

                {/* Download & Reset buttons */}
                <DownloadButton pdfBlob={pdfBlob} onReset={onReset} />
            </div>
        </div>
    );
}
